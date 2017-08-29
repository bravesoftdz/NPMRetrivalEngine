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
import metasearch.cache.CacheRankingManager;
import ner.EntityExtractor;
import ranking.RankedItem;
import ranking.Ranking;

public class GoogleWrapper extends SearchWrapperAbs implements Searcher {
	
	public static final String GOOGLE = "google.com";
	
	// We need a real browser user agent or Google will block our request with a
	// 403 - Forbidden
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	public int RESULTS = 20;
	
	private EntityExtractor ent_extractor; //Named Entity Recognition

	public static void main(String[] args) throws Exception {

	}

	public GoogleWrapper(int results, EntityExtractor ent_ext) {
		RESULTS = results;
		ent_extractor = ent_ext;
	}

	@Override
	public List<String> downloadResultContent(String query, Proxy proxy){
		List<String> results = new ArrayList<String>();
			Document doc = null;
			try {
				if (proxy != null) {
					doc = Jsoup.connect("https://google.com/search?q=" + query + "&num=" + RESULTS).proxy(proxy)
							.userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup.connect("https://google.com/search?q=" + query + "&num=" + RESULTS)
							.userAgent(USER_AGENT).timeout(0).get();
				}
			} catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}
			if(doc != null){		
				for (Element result : doc.select("h3.r a")) {
	
					final String url = result.attr("href");
					System.out.println(url);
	
					Document tech = null;
					try {
						if (proxy != null) {
							tech = Jsoup.connect(url).proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
						} else {
							tech = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0).get();
						}
						results.add(tech.toString());
					} catch (Exception e) {
						System.out.println("Error en: " + url);
					}
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
	
	public Ranking processData(List<String> contents, EntityExtractor ent_extractor2) {
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		
		List<RankedItem> ranking = new ArrayList<RankedItem>();

		int rank = 1;
			
		for (int i = 0; i <contents.size() && ranking.size() < RESULTS; i++) {
			
			String content = contents.get(i);
			
			Document tech = (Document) Jsoup.parse((String)content);
			tech.outputSettings(new Document.OutputSettings().prettyPrint(false));

			tech.select("br").append("\\n");
			tech.select("p").prepend("\\n\\n");
			String text = "";
			text = tech.html().replaceAll("\\\\n", "\n");
			text = Jsoup.clean(text, "", Whitelist.none(),
					new Document.OutputSettings().prettyPrint(false));
			
			List<String> entities = ent_extractor.getNamedEntities(text);

			for (String entity : entities) {
				if (!ranking.contains(entity)) {
					ranking.add(new RankedItem(entity, (double) (RESULTS - (rank - 1))));
					wordCount.put(entity, rank);
				}
			}
			rank++;
		}
		
		return new Ranking(ranking);
	}

	public List<String> acquireData(String query, Proxy proxy){
		
		String query2 = "javascript package " + query;
		
		System.out.println("Acquiring data from google...");

		List<String> content = getResultContent(query2, proxy,this);
		
		System.out.print(" ...connection SUCCESSFUL...");
		
		return content;
	}

	@Override
	public String getName() {
		return GOOGLE+"_"+ent_extractor.getTechniqueName();
	}

}