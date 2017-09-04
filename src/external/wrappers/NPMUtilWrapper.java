package external.wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import metasearch.cache.CacheContentManager;
import util.ConfigManager;
import util.PackageManager;

public class NPMUtilWrapper{

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

	public NPMUtilWrapper() {
	}
	
	public static String downloadReadmeContent(String query, Proxy proxy){
		String result = "";
		Document doc = null;
			try {
				System.out.println("Downloading "+query);
				if (proxy != null) {
					doc = Jsoup.connect(
							"https://www.npmjs.com/package/" + query)
							.proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup.connect(
							"https://www.npmjs.com/package/" + query)
							.userAgent(USER_AGENT).timeout(0).get();
				}
				if (doc.select("div#readme") != null) {
					result = doc.select("div#readme").text();
				}
			} catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}

		return result;
	}
	
	public static void createPackageNameFile(String npm_file_path) {
		String npm_file = null;
		if(npm_file_path!=null){
			npm_file = npm_file_path;
		}else{
			npm_file = ConfigManager.getInstance().getProperty("json_file_path");
		}
		String pkg_file = ConfigManager.getInstance().getProperty("pkg_name_file");
	    try {
	      FileReader fr = new FileReader(npm_file);
	      BufferedReader br = new BufferedReader(fr);
	      FileWriter ficheroNombres= new FileWriter(pkg_file);
	      String linea;
	      Integer count = 0;
	      while((linea = br.readLine()) != null){
	    	  String[] split = linea.split("\\{\"id\":\"");
	    	  if(split.length>1){
	    		  String name = split[1].split("\",\"")[0];
	    		  if(name!=null){
	    			  try{
	    				  ficheroNombres.write(name + "\r\n");
	    				  System.out.println(name);
	    			  }catch(Exception e){
	    				  
	    			  }
	    		  }

	    	      //System.out.println(getDescription(linea));
	    	  }
	    	  
	      }     
		  ficheroNombres.close();
	      fr.close();      
	    }
	    catch(Exception e) {
	      System.out.println("Excepcion leyendo fichero "+ npm_file + ": " + e);
	    }
		
		
	}

	public static void downloadAllReadmeContent(Proxy proxy) {
		for(String pkg:PackageManager.getInstance().getPkgNames()){
			String readme_dir = ConfigManager.getInstance().getProperty("readme_dir");
			File directory = new File(readme_dir);
			directory.mkdirs();
			CacheContentManager.getInstance().saveFileContent(downloadReadmeContent(pkg, proxy),readme_dir,pkg+".txt");
		}
		
	}

}