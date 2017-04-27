package metasearch;

import java.net.Proxy;

import ranking.Ranking;

public interface Searcher {
	public String getName();
	public Ranking search(String searchQuery,Proxy proxy);
}
