package test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import external.wrappers.GoogleWrapper;
import metasearch.cache.CacheContentManager;
import ner.HiperlinkMatching;
import ner.StringMatching;
import ranking.RankedItem;
import ranking.Ranking;
import util.ConfigManager;
import util.PackageManager;

    

public class TestNER {
	
	static int max_results = Integer.valueOf(ConfigManager.getInstance().getProperty("max_results"));
	
	public static void main(String[] args) {
		

		
		/*GoogleWrapper google = new GoogleWrapper(max_results,new HiperlinkMatching());
		List<String> data = CacheContentManager.getInstance().loadContentFromCache(google,"extract barcode from image");
		Ranking ranking = google.processData(data,new StringMatching());//TODO data null
		for (RankedItem i : ranking.getRankingList()) {
			System.out.println(i.getName()+":"+i.getScore());			
		}*/
		
		String html = CacheContentManager.getInstance().loadFileContent(new File("content_cache/google.com_str_match/extract barcode from image/4.txt"));

		String cleaned = //Jsoup.clean(html, "http://base.uri", Whitelist.relaxed().preserveRelativeLinks(true));
		
		Jsoup.clean(html, "", Whitelist.basic(),
				new Document.OutputSettings().prettyPrint(false));
		
		//System.out.println(cleaned);
		
		HiperlinkMatching hm = new HiperlinkMatching();
		List<String> entities = hm.getNamedEntities(html);
		for(String entity:entities){
			System.out.println(entity+" -> "+PackageManager.getInstance().getHomePage(entity));
		}
		

	}

}
