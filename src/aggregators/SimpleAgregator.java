package aggregators;

import java.util.ArrayList;
import java.util.List;

public class SimpleAgregator implements Aggregator {

	@Override
	public List<String> aggregate(List<List<String>> rankings) {

		List<String> result = new ArrayList<String>();
		if (rankings != null && rankings.size() > 0) {
			for (String pkg : rankings.get(0)) {
				if (rankings.size() > 1) {
					for (int i = 1; i < rankings.size(); i++) {
						if (rankings.get(i).contains(pkg)) {
							result.add(pkg);
							break;
						}
					}
				}
			}
		}
		return result;
	}

}
