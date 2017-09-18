package metasearch;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aggregators.Aggregator;
import metasearch.cache.CacheContentManager;
import metasearch.cache.CacheRankingManager;
import ner.EntityExtractor;
import ranking.Ranking;

public class MetaSearcherImp implements MetaSearcher{
	
	List<Searcher> searchers;
	Aggregator aggregator;
	
	HashMap<String,List<String>> rankings;
	
	public MetaSearcherImp(List<Searcher> searchers, Aggregator aggregator) {
		super();
		this.searchers = searchers;
		this.aggregator = aggregator;
	}

	public Ranking search(String query, Proxy proxy, List<Searcher> searchers, Aggregator aggregator)
			throws IOException {

		List<Ranking> rankings = new ArrayList<Ranking>();
		
		for (Searcher searcher : searchers) {
			rankings.add(searcher.search(query, proxy));
		}

		return aggregator.aggregate(rankings);
	}
	
	public Ranking search(String query, Proxy proxy){

		List<Ranking> rankings = new ArrayList<Ranking>();
		
		for (Searcher searcher : searchers) {
			rankings.add(searcher.search(query, proxy));
		}

		return aggregator.aggregate(rankings);
	}

	@Override
	public String getId() {
		return aggregator.getName();
	}
	
	@Override
	public String getContentId() {
		return aggregator.getName();
	}

	@Override
	public List<String> acquireData(String query, Proxy proxy) {
		
		rankings = new HashMap<String,List<String>>();
		
		for (Searcher searcher : searchers) {
	        List<String> data = CacheContentManager.getInstance().loadContentFromCache(searcher,query);
	        if(data==null){
	        	data = searcher.acquireData(query,proxy);
	        }
			rankings.put(searcher.getId(),data);
		}

		return null;
	}

	@Override
	public Ranking processData(List<String> contents) {
		List<Ranking> rs = new ArrayList<Ranking>();
		
		for (Searcher searcher : searchers) {
			rs.add(searcher.processData(rankings.get(searcher.getId())));
		}

		return aggregator.aggregate(rs);
	}
	
	public Ranking processData(List<String> contents, EntityExtractor ent_extractor, String query) {
		List<Ranking> rs = new ArrayList<Ranking>();
		
		for (Searcher searcher : searchers) {
			Ranking ranking = CacheRankingManager.getInstance().loadRankingFromCache(searcher, query);
			if (ranking == null) {
				ranking = searcher.processData(rankings.get(searcher.getId()));
			}
			rs.add(ranking);
		}

		return aggregator.aggregate(rs);
	}
}