package metasearch;

import java.net.Proxy;
import java.util.List;

public interface Searcher {
	public String getName();
	public List<String> search(String searchQuery,Proxy proxy);
}
