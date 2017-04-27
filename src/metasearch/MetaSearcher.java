package metasearch;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import ranking.Ranking;

public interface MetaSearcher {
	
	public Ranking search(String query, Proxy proxy, List<Searcher> searchers, Aggregator aggregator) throws IOException, ParseException;
	
	public Ranking search(String query, Proxy proxy) throws IOException, ParseException;

}
