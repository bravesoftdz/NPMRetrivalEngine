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
import org.jsoup.select.Elements;

import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ner.EntityExtractor;
import ner.StringMatching;
import ranking.RankedItem;
import ranking.Ranking;
import util.PackageManager;
import util.StopWordManager;

public class BingWrapper extends SearchWrapperAbs implements Searcher {
	// We need a real browser user agent or Google will block our request with a
	// 403 - Forbidden
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	public int MAX_PAGE = 2;
	
	private EntityExtractor ent_extractor; //Named Entity Recognition

	public static void main(String[] args) throws Exception {

	}

	public BingWrapper(int results) {
		MAX_PAGE = (int) Math.ceil(results / 10);
		ent_extractor = new StringMatching();
	}

	@Override
	public List downloadResultContent(String query, Proxy proxy){
		List result = new ArrayList();
		for (int page = 1; page < MAX_PAGE; page++) {
			Document doc;
			try {
				int param_first = ((page - 1) * 10) + 1;
				if (proxy != null) {
					doc = Jsoup
							.connect("https://www.bing.com/search?q=" + query
									+ (param_first != 1 ? ("&first=" + param_first) : ""))
							.proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup
							.connect("https://www.bing.com/search?q=" + query
									+ (param_first != 1 ? ("&first=" + param_first) : ""))
							.userAgent(USER_AGENT).timeout(0).get();
				}
				result.add(doc);
			}catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}
		}
		return result;
	}
	
	public Ranking search(String query, Proxy proxy) {

		Ranking r = CacheRankingManager.getInstance().loadRankingFromCache(this, query);

		if (r != null) {
			return r;
		} else {

			String query2 = "javascript package " + query;
			
			HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

			List<RankedItem> ranking = new ArrayList<RankedItem>();

			int rank = 1;
			// Fetch the page
			Document doc;

			System.out.println("Starting connection with bing...");
			System.out.println("Analizing Results...");

			List contents = downloadResultContent(query2, proxy);
			
			for (int page = 0; page < contents.size(); page++) {
				
				doc = (Document)contents.get(page);
					PackageManager pkgm = PackageManager.getInstance();					StopWordManager swm = StopWordManager.getInstance();
					Elements elements = doc.select("h2 a");
					for (int i = 1; i < elements.size() - 1; i++) {						Element result = elements.get(i);						// final String title = result.text();						final String url = result.attr("href");
						// Now do something with the results (maybe something						// more						// useful than just printing to console)
						System.out.println(url);
						String text = "";						Document tech = null;						try {							if (proxy != null) {								tech = Jsoup.connect(url).proxy(proxy).userAgent(USER_AGENT).timeout(0).get();							} else {								tech = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();							}							tech.outputSettings(new Document.OutputSettings().prettyPrint(false));							tech.select("br").append("\\n");							tech.select("p").prepend("\\n\\n");							text = tech.html().replaceAll("\\\\n", "\n");							text = Jsoup.clean(text, "", Whitelist.none(),									new Document.OutputSettings().prettyPrint(false));
							
							List<String> entities = ent_extractor.getNamedEntities(text);

							for (String entity : entities) {
								if (!ranking.contains(entity)) {
									ranking.add(new RankedItem(entity, (double) (((page - 1) * 10) + i)));
									wordCount.put(entity, rank);
								}
							}
													} catch (Exception e) {							System.out.println("Error en: " + url);						}						rank++;					}
					// TFIDFCalculator calculator = new TFIDFCalculator();					/*					 * for(String key:wordCount.keySet()){ double tfidf =					 * calculator.tfIdf(wdocs.get(2), wdocs, key);					 * results.add(key); // System.out.println(key + " " +					 * tfidf); }					 */										r =  new Ranking(ranking);					CacheRankingManager.getInstance().saveRankingInCache(r, this, query);

			}
		}

		/*
		 * for (RankedItem key : ranking) { // System.out.println(key + ": " +
		 * wordCount.get(key)); }
		 */

		return r;

	}

	@Override
	public String getName() {
		return "bing.com";
	}

}