package external.wrappers;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ranking.RankedItem;
import ranking.Ranking;

public class NPMWrapper extends SearchWrapperAbs implements Searcher {
	// We need a real browser user agent or Google will block our request with a
	// 403 - Forbidden
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

	public int MAX_PAGE = 20;
	public String RANK = OPTIMAL;

	public static final String OPTIMAL = "optimal";
	public static final String QUALITY = "quality";
	public static final String POPULARITY = "popularity";
	public static final String MAINTENANCE = "maintenance";

	public NPMWrapper(int results, String ranktype) {
		MAX_PAGE = (int) Math.ceil(results / 10);
		RANK = ranktype;
	}
	
	@Override
	public List downloadResultContent(String query, Proxy proxy){
		List result = new ArrayList();
		for (int page = 1; page < MAX_PAGE; page++) {
			Document doc = null;
			try {
				if (proxy != null) {
					doc = Jsoup.connect(
							"https://www.npmjs.com/search?q=" + query + "&page=" + page + "&ranking=" + RANK)
							.proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup.connect(
							"https://www.npmjs.com/search?q=" + query + "&page=" + page + "&ranking=" + RANK)
							.userAgent(USER_AGENT).timeout(0).get();
				}
				result.add(doc);
			} catch (IOException e1) {
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

			List<RankedItem> results = new ArrayList<RankedItem>();

			Document doc;

			System.out.println("Starting connection with NPM...");
			System.out.println("Analizing Results...");

			List contents = downloadResultContent(query, proxy);
			
			for (int page = 0; page < contents.size(); page++) {
				
				doc = (Document)contents.get(page);
				// System.out.println("Connection with NPM finished...");

				if (doc.select("h3 a").size() == 0) {
					r = new Ranking(results);
					break;
				}

				Elements elements = doc.select("h3 a");
				for (int e = 0; e < elements.size(); e++) {
					Element result = elements.get(e);

					if (result.className().startsWith("packageName")) {

						final String title = result.text();
						final String url = result.attr("href");

						// Now do something with the results (maybe
						// something
						// more useful than just printing to console)

						System.out.println(title + " -> " + url);

						results.add(new RankedItem(title, (double) (((page - 1) * 10) + e)));

					}
				}
				
				r = new Ranking(results);
				CacheRankingManager.getInstance().saveRankingInCache(r, this, query);

			}
		}

		return r;

	}

	@Override
	public String getName() {
		return "npmjs.com" + RANK + MAX_PAGE;
	}

}