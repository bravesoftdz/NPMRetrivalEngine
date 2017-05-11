package external.wrappers;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ranking.RankedItem;
import ranking.Ranking;

public class NPMSearchWrapper implements Searcher {
	// We need a real browser user agent or Google will block our request with a
	// 403 - Forbidden
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	public int MAX_RESULTS = 20;

	public NPMSearchWrapper(int results) {
		this.MAX_RESULTS = results;
	}

	public Ranking search(String query, Proxy proxy) {

		Ranking r = CacheRankingManager.getInstance().loadRankingFromCache(this, query);

		if (r != null) {
			return r;
		} else {

			List<RankedItem> results = new ArrayList<RankedItem>();
			System.out.println("Starting connection with NPMSearch...");
			System.out.println("Analizing Results...");

			String json = null;
			try {

				if (proxy != null) {
					json = Jsoup.connect("http://npmsearch.com/query?fields=name&sort=rating:desc&q=" + query + "&rows="
							+ MAX_RESULTS).proxy(proxy).ignoreContentType(true).execute().body();
				} else {
					json = Jsoup.connect("http://npmsearch.com/query?fields=name&sort=rating:desc&q=" + query + "&rows="
							+ MAX_RESULTS).ignoreContentType(true).execute().body();
				}
				// System.out.println("Connection with NPM finished...");
				JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
				JsonArray names = obj.getAsJsonArray("results");
				int rank = 1;
				for (JsonElement entry : names) {
					if (entry != null && entry.isJsonObject()) {
						results.add(new RankedItem(((JsonObject) entry).get("name").getAsString(),
								(double) (MAX_RESULTS - (rank - 1))));
						System.out.println();
					}
					rank++;
				}
				r = new Ranking(results);
				CacheRankingManager.getInstance().saveRankingInCache(r, this, query);

			} catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}
		}

		return r;

	}

	@Override
	public String getName() {
		return "npmsearch.com";
	}

}