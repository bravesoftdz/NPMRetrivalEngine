package metasearch;

import java.net.Proxy;
import java.util.List;

import ner.EntityExtractor;
import ranking.Ranking;

public interface Searcher {
	
	public String getName();
	public Ranking search(String searchQuery,Proxy proxy);
	
	public List<String> acquireData(String query, Proxy proxy);
	public Ranking processData(List<String> contents, EntityExtractor ent_extractor);
	
}
