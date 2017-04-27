package external.wrappers;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import metasearch.Searcher;
import ranking.RankedItem;
import ranking.Ranking;
import util.PackageManager;
import util.StopWordManager;

public class GoogleWrapper implements Searcher
{
    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	public int RESULTS = 20;
    
    public static void main(String[] args) throws Exception {
    	
    	
               
    }
    
    public GoogleWrapper(int results) {
    	RESULTS = results;
    }
    
    public Ranking search(String query, Proxy proxy){
    	
    	HashMap<String, Integer> wordCount = new HashMap<String,Integer>();
    	
		List<RankedItem> ranking = new ArrayList<RankedItem>();
    	
    	query = "javascript package " + query;
    	
    	int rank = 1;
        //Fetch the page
        Document doc;
		try {
			System.out.println("Starting connection with google...");
			
			if(proxy!=null){
				doc = Jsoup.connect("https://google.com/search?q="+query+"&num=" + RESULTS).proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
			}else{
				doc = Jsoup.connect("https://google.com/search?q="+query+"&num=" + RESULTS).userAgent(USER_AGENT).timeout(0).get();
			}

	        PackageManager pkgm = PackageManager.getInstance();
	        StopWordManager swm = StopWordManager.getInstance();
	        
	        
	        System.out.println("Analizing Results...");
	        for (Element result : doc.select("h3.r a")){
	
	            //final String title = result.text();
	            final String url = result.attr("href");
	
	            //Now do something with the results (maybe something more useful than just printing to console)
	
	            System.out.println(url);
	            
	            String text = "";
	            Document tech = null;
	            try{
					if (proxy != null) {
						tech = Jsoup.connect(url).proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
					} else {
						tech = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();
					}
	            	tech.outputSettings(new Document.OutputSettings()
	                        .prettyPrint(false));// makes html() preserve linebreaks and
	                                                    // spacing
	                tech.select("br").append("\\n");
	                tech.select("p").prepend("\\n\\n");
	                text = tech.html().replaceAll("\\\\n", "\n");
	                text = Jsoup.clean(text, "", Whitelist.none(),
	                        new Document.OutputSettings().prettyPrint(false));
	                
	                String[] words = text.split(" ");
	                
	                for(String word:words){
	                	word=word.trim().toLowerCase();
	                	if(!"".equals(word) && !swm.isStopWord(word) && pkgm.isPkgName(word)){
	                		if(!ranking.contains(word)){
	                			ranking.add(new RankedItem(word, (double)(RESULTS-(rank-1))));
	                			wordCount.put(word, rank);
	                		}
	                	}
	                }
	            }catch(Exception e){
	            	System.out.println("Error en: "+url);
	            }       
	            rank++;
	        }
	        
	       // TFIDFCalculator calculator = new TFIDFCalculator();
	       /* for(String key:wordCount.keySet()){
	            double tfidf = calculator.tfIdf(wdocs.get(2), wdocs, key);
	            results.add(key);
	           // System.out.println(key + " " + tfidf);
	        }*/
        
		} catch (IOException e1) {
			System.out.println("Error estableciendo conexion con google.");;
		}    

        /*for(RankedItem key:ranking){
//        	System.out.println(key+": "+wordCount.get(key));
        }*/
		
		return new Ranking(ranking);

    }

	@Override
	public String getName() {
		return "google.com";
	}

}