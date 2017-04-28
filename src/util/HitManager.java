package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HitManager {

	private static HitManager instance = null;
	private HashMap<String, List<String>> queriesXhits = null;

	protected HitManager() {
		queriesXhits = new HashMap<String, List<String>>();
		loadQueriesXhits();
	}

	public static HitManager getInstance() {
		if (instance == null) {
			instance = new HitManager();
		}
		return instance;
	}

	protected void loadQueriesXhits() {
		String fichero = "examples/queriesXhits";
		try {
			FileReader fr = new FileReader(fichero);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] queriesXhits = linea.split(":=:");
				String q = queriesXhits[0];
				List<String> hits = "".equals(queriesXhits[1])?new ArrayList<String>():Arrays.asList(queriesXhits[1].split(","));
				this.queriesXhits.put(q, hits);
			}
			fr.close();
		} catch (Exception e) {
			System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
		}
	}
	
	public Set<String> getQueries(){
		return queriesXhits.keySet();
	}
	
	public void addHit(String query, String hit){
		if(queriesXhits.get(query)!=null){
			if(!queriesXhits.get(query).contains(hit)){
				queriesXhits.get(query).add(hit);
			}
		}else{
			queriesXhits.put(query, new ArrayList<String>());
			queriesXhits.get(query).add(hit);
		}
	}

	public boolean isAHit(String q, String pkg) {
		if (queriesXhits.get(q) != null && queriesXhits.get(q).contains(pkg)) {
			return true;
		}
		return false;
	}

}