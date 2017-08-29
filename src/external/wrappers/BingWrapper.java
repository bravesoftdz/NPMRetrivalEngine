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
	private static final String BING = "bing.com";
	public int MAX_PAGE = 2;
	public int RESULTS = 20;
	
	private EntityExtractor ent_extractor; //Named Entity Recognition

	public static void main(String[] args) throws Exception {

	}

	public BingWrapper(int results, EntityExtractor ent_ext) {
		RESULTS = results;
		MAX_PAGE = (int) Math.ceil(results / 10);
		ent_extractor = ent_ext;
	}

	@Override
	public List<String> downloadResultContent(String query, Proxy proxy){
		List<String> results = new ArrayList<String>();
		for (int page = 1; page <= MAX_PAGE; page++) {
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

				Elements elements = doc.select("h2 a");

				for (int i = 1; i < elements.size() - 1; i++) {
					Element result = elements.get(i);
					// final String title = result.text();
					final String url = result.attr("href");

					System.out.println(url);

					String text = "";
					Document tech = null;
					try {
						if (proxy != null) {
							tech = Jsoup.connect(url).proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
						} else {
							tech = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();
						}		
						results.add(tech.toString());
					}catch (Exception e) {
						System.out.println("Error en: " + url);
					}
				}
			}catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}
		}
		return results;
	}
	
	public Ranking search(String query, Proxy proxy) {
		
		Ranking r = CacheRankingManager.getInstance().loadRankingFromCache(this, query);

		if (r != null) {
			return r;
		} else {

			List<String> contents = acquireData(query, proxy);
			
			r = processData(contents,ent_extractor);
			
			CacheRankingManager.getInstance().saveRankingInCache(r, this, query);

		}

		return r;
	}
	
	
	@Override
	public Ranking processData(List<String> contents, EntityExtractor ent_extractor) {
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

		List<RankedItem> ranking = new ArrayList<RankedItem>();

		int rank = 1;

		System.out.println("Analizing Results...");

		for (int i = 0; ranking.size() < RESULTS && i <contents.size(); i++) {		
			String content = contents.get(i);
			Document tech = (Document) Jsoup.parse((String)content);			tech.outputSettings(new Document.OutputSettings().prettyPrint(false));			tech.select("br").append("\\n");			tech.select("p").prepend("\\n\\n");
			String text="";			text = tech.html().replaceAll("\\\\n", "\n");			text = Jsoup.clean(text, "", Whitelist.none(),					new Document.OutputSettings().prettyPrint(false));
			
			List<String> entities = ent_extractor.getNamedEntities(text);

			for (String entity : entities) {
				if (!ranking.contains(entity)) {
					ranking.add(new RankedItem(entity, (double) (RESULTS - (rank - 1))));
					wordCount.put(entity, rank);
				}
			}			rank++;		}
				return new Ranking(ranking);

	}
	
	public List<String> acquireData(String query, Proxy proxy){
		
		String query2 = "javascript package " + query;
		
		System.out.println("Acquiring data from Bing...");

		List<String> content = getResultContent(query2, proxy,this);
		
		System.out.print(" ...connection SUCCESSFUL...");
		
		return content;
	}

	@Override
	public String getName() {
		return BING+"_"+ent_extractor.getTechniqueName();
	}


}