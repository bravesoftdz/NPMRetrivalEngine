package metasearch;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import metasearch.cache.CacheManager;

public class MetaSearcherWithCache implements MetaSearcher{
	
	List<Searcher> searchers;
	Aggregator aggregator;
	
	public MetaSearcherWithCache(List<Searcher> searchers, Aggregator aggregator) {
		super();
		this.searchers = searchers;
		this.aggregator = aggregator;
	}

	public List<String> search(String query, Proxy proxy, List<Searcher> searchers, Aggregator aggregator)
			throws IOException, ParseException {

		List<List<String>> rankings = new ArrayList<List<String>>();
		
		CacheManager cache = CacheManager.getInstance();
		
		for (Searcher searcher : searchers) {
			if(cache.isCached(searcher, query)){
				rankings.add(cache.getCached(searcher, query));
			}else{
				List<String> ranking = searcher.search(query, proxy);
				rankings.add(ranking);
				cache.save(searcher,query, ranking);
			}
		}
		
		cache.saveCache();

		return aggregator.aggregate(rankings);
	}
	
	public List<String> search(String query, Proxy proxy)
			throws IOException, ParseException {

		List<List<String>> rankings = new ArrayList<List<String>>();
		
		CacheManager cache = CacheManager.getInstance();
		
		for (Searcher searcher : searchers) {
			if(cache.isCached(searcher, query)){
				rankings.add(cache.getCached(searcher, query));
			}else{
				List<String> ranking = searcher.search(query, proxy);
				rankings.add(ranking);
				if(ranking.size()!=0){
					cache.save(searcher,query, ranking);
				}
			}
		}
		
		cache.saveCache();

		return aggregator.aggregate(rankings);
	}
}