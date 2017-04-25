package aggregators;

import java.util.List;

public interface Aggregator {
	public List<String> aggregate(List<List<String>> rankings);
}
