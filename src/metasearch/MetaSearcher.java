package metasearch;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;

public interface MetaSearcher {
	
	public List<String> search(String query, Proxy proxy, List<Searcher> searchers, Aggregator aggregator) throws IOException, ParseException;
	
	public List<String> search(String query, Proxy proxy) throws IOException, ParseException;

}
