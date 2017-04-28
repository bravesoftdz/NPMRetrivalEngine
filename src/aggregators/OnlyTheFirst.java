package aggregators;

import java.util.List;

import ranking.Ranking;

public class OnlyTheFirst implements Aggregator {

	protected String name = "OnlyTheFirst";

	@Override
	public Ranking aggregate(List<Ranking> rankings) {
		return rankings.get(0);

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

}
