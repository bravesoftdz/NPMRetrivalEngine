package google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import util.PackageManager;
import util.StopWordManager;
import util.TFIDFCalculator;

public class ScanGoogleTechResults 
{
    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    public static void main(String[] args) throws Exception {
    	
    	String query = "javascript library extract barcode from image file";

        //Fetch the page
        final Document doc = Jsoup.connect("https://google.com/search?q="+query+"&num=20").userAgent(USER_AGENT).get();

        //Traverse the results
        //Traverse the results
        PackageManager pkgm = PackageManager.getInstance();
        StopWordManager swm = StopWordManager.getInstance();
        HashMap<String, Integer> wordCount = new HashMap<String,Integer>();
        List<List<String>> wdocs = new ArrayList<List<String>>();
        
        for (Element result : doc.select("h3.r a")){

            final String title = result.text();
            final String url = result.attr("href");

            //Now do something with the results (maybe something more useful than just printing to console)

            System.out.println(title + " -> " + url);
            
            String text = "";
            Document tech = Jsoup.connect(url).userAgent(USER_AGENT).get();
            tech.outputSettings(new Document.OutputSettings()
                    .prettyPrint(false));// makes html() preserve linebreaks and
                                                // spacing
            tech.select("br").append("\\n");
            tech.select("p").prepend("\\n\\n");
            text = tech.html().replaceAll("\\\\n", "\n");
            text = Jsoup.clean(text, "", Whitelist.none(),
                    new Document.OutputSettings().prettyPrint(false));
            
            String[] words = text.split(" ");
            
            List<String> wdoc = new ArrayList<String>();
            for(String word:words){
            	word=word.trim().toLowerCase();
            	if(!"".equals(word) && !swm.isStopWord(word) && pkgm.isPkgName(word)){
            		wdoc.add(word);
                	if(wordCount.get(word)==null){
                		wordCount.put(word, 1);
                	}
                	wordCount.put(word, wordCount.get(word)+1);
            		//System.out.println(word+": "+wordCount.get(word));
            	}
            }
            wdocs.add(wdoc);
            //System.out.println(text);
            //break;
            
        }
        
        TFIDFCalculator calculator = new TFIDFCalculator();
        for(String key:wordCount.keySet()){
            double tfidf = calculator.tfIdf(wdocs.get(2), wdocs, key);
            System.out.println(key + " " + tfidf);
        }

        /*for(String key:wordCount.keySet()){
        	System.out.println(key+": "+wordCount.get(key));
        }*/
               
    }

}