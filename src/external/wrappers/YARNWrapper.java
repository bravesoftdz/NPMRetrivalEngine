package external.wrappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import metasearch.Searcher;
import util.PackageManager;
import util.StopWordManager;
import util.TFIDFCalculator;

@Deprecated
public class YARNWrapper implements Searcher
{
    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    
    public List<String> search(String query){
    	
    	List<String> results = new ArrayList<String>();
    	
        Document doc;
		try {
			System.out.println("Starting connection with YARN...");
			doc = Jsoup.connect("https://yarnpkg.com/packages?q=user").userAgent(USER_AGENT).timeout(0).get();
//			System.out.println("Connection with NPM finished...");

	        
	        System.out.println("Analizing Results...");
	        System.out.println(doc.toString());
	        for (Element result : doc.select("div")){
	        	//System.out.println(result.className());
	        	if(result.className().startsWith("ais-Hit--item")){	        		
	        
		            final String title = result.text();
		            final String url = result.attr("href");
		
		            //Now do something with the results (maybe something more useful than just printing to console)
		
		            System.out.println(title + " -> " + url); 
		            
		            results.add(title);
		            
	        	}
	        }
	        
        
		} catch (IOException e1) {
			System.out.println("Error estableciendo conexion con YARN.");;
		}
        
		return results;

    }

	@Override
	public String getName() {
		return "yarn.com";
	}

}