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
import ranking.RankedItem;
import ranking.Ranking;

public class NPMWrapper implements Searcher {
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
		MAX_PAGE = (int) Math.ceil(results/10);
		RANK = ranktype;
	}

	public Ranking search(String query, Proxy proxy) {
		
		List<RankedItem> results = new ArrayList<RankedItem>();

		Document doc;
		
		System.out.println("Starting connection with NPM...");
		System.out.println("Analizing Results...");

		for (int page = 1; page < MAX_PAGE; page++) {
			try {

				if (proxy != null) {
					doc = Jsoup
							.connect("https://www.npmjs.com/search?q=" + query + "&page=" + page + "&ranking=" + RANK)
							.proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup
							.connect("https://www.npmjs.com/search?q=" + query + "&page=" + page + "&ranking=" + RANK)
							.userAgent(USER_AGENT).timeout(0).get();
				}
				// System.out.println("Connection with NPM finished...");


				if (doc.select("h3 a").size() == 0) {
					break;
				}
				
				Elements elements = doc.select("h3 a");
				for (int e=0; e< elements.size(); e++) {
					Element result = elements.get(e);

					if (result.className().startsWith("packageName")) {

						final String title = result.text();
						final String url = result.attr("href");

						// Now do something with the results (maybe something
						// more useful than just printing to console)

						System.out.println(title + " -> " + url);

						results.add(new RankedItem(title, (double)(((page-1)*10)+e)));

					}
				}

			} catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}
		}

		return new Ranking(results);

	}

	@Override
	public String getName() {
		return "npmjs.com" + RANK + MAX_PAGE;
	}

}