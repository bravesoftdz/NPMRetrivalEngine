package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryManager {
	
	   private static QueryManager instance = null;
	   private HashMap<String,List<String>> queries = null;
	   
	   protected QueryManager() {
		   queries = new HashMap<String,List<String>>();
		   loadQueries();
	   }
	   public static QueryManager getInstance() {
	      if(instance == null) {
	         instance = new QueryManager();
	      }
	      return instance;
	   }
	   
	   protected void loadQueries() {
			String fichero = "examples/queriesXhits.txt";
		    try {
		      FileReader fr = new FileReader(fichero);
		      BufferedReader br = new BufferedReader(fr);
		      String linea;
		      while((linea = br.readLine()) != null){
		    	 String[] queriesXhits = linea.split(":=:");
		    	 String q = queriesXhits[0];
		    	 List<String> hits = Arrays.asList(queriesXhits[1].split(","));
		    	 queries.put(q,hits);	    	  
		      }     
		      fr.close();      
		    }
		    catch(Exception e) {
		      System.out.println("Excepcion leyendo fichero "+ fichero + ": " + e);
		    }	
		}
	   
	   public boolean isAHit(String q, String pkg){
		   if(queries.get(q)!=null && queries.get(q).contains(pkg)){
			   return true;
		   }
		   return false;
	   }
	   
	}