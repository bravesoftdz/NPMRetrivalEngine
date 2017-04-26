package metasearch;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;

public class MetaSearcherOnline implements MetaSearcher{
	
	List<Searcher> searchers;
	Aggregator aggregator;
	
	public MetaSearcherOnline(List<Searcher> searchers, Aggregator aggregator) {
		super();
		this.searchers = searchers;
		this.aggregator = aggregator;
	}

	public List<String> search(String query, Proxy proxy, List<Searcher> searchers, Aggregator aggregator)
			throws IOException, ParseException {

		List<List<String>> rankings = new ArrayList<List<String>>();
		
		for (Searcher searcher : searchers) {
			rankings.add(searcher.search(query, proxy));
		}

		return aggregator.aggregate(rankings);
	}
	
	public List<String> search(String query, Proxy proxy)
			throws IOException, ParseException {

		List<List<String>> rankings = new ArrayList<List<String>>();
		
		for (Searcher searcher : searchers) {
			rankings.add(searcher.search(query, proxy));
		}

		return aggregator.aggregate(rankings);
	}
}