package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import util.PackageManager;

public class TestBrowserStringMatching {

	public static void main(String[] args) {
		
		String dataDir = "C:/npm_data/documentsOriginal/documents/";	
		
		String query = "https://coveralls.io/";
		
		System.out.println();
		System.out.println("RESULTADOS");
		List<String> results = getCoverage(dataDir, query);
		
		for(String tech:results){
				System.out.println("<a href=\""+"https://www.npmjs.com/package/"+tech+"\">"+tech+"</a>");
		}

	}

	private static List<String> getCoverage(String dataDir, String query) {
		
		List<String> technologies = PackageManager.getInstance().getPkgNames();
		List<String> results = new ArrayList<String>();
		
		for(String tech:technologies){
				System.out.println(tech);
					if(!"aux".equals(tech)){
					String fichero = dataDir + tech + ".txt";
					FileReader fr = null;
					BufferedReader br = null;
					try {
		
						fr = new FileReader(fichero);
						br = new BufferedReader(fr);
						String linea;
						
						while ((linea = br.readLine()) != null) {
							System.out.print(".");
							if(linea.contains(query)){
								results.add(tech);						
								break;
							}
						}
	
					} catch (Exception e) {
						System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
					} finally {
						try {
							if (null != fr)
								fr.close();
						} catch (Exception e2) {
							System.out.println("Excepcion cerrando fichero " + fichero + ": " + e2);
						}
					}
				}

		}
		
		return results;
	}

}
