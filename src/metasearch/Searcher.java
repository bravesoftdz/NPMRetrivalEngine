package metasearch;

import java.util.List;

public interface Searcher {
	public String getName();
	public List<String> search(String searchQuery);
}
