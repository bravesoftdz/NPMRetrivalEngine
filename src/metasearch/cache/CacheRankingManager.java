package metasearch.cache;

import java.util.HashMap;

import metasearch.Searcher;
import ranking.Ranking;

public class CacheRankingManager {

	private static CacheRankingManager instance = null;
	private HashMap<String, Ranking> cache = null;

	protected CacheRankingManager() {
		//cache = new HashMap<String, Ranking>();
	}

	public static CacheRankingManager getInstance() {
		if (instance == null) {
			instance = new CacheRankingManager();
		}
		return instance;
	}

	public Ranking loadRankingFromCache(Searcher searcher, String query) {
		Ranking ranking = new Ranking(searcher.getName() +"_"+ query,"web_cache/" + searcher.getName() +"/"+ query + ".txt");
		if(ranking.size()>0){
			return ranking;
		}
		return null;
	}

	public void saveRankingInCache(Ranking ranking, Searcher searcher, String query) {
		ranking.saveRankingInFile("web_cache/" + searcher.getName() +"/"+ query + ".txt");
	}

}
