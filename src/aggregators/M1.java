package aggregators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ranking.RankedItem;
import ranking.Ranking;

public class M1 extends MarkovChainAggregator {

	@Override
	public String getName() {
		return "M1";
	}

	@Override
	public void setName(String name) {}

	@Override
	public double[][] getTransitionMatrix(List<RankedItem> uniqueItems, List<Ranking> rankings) {
		
		double[][] transitionMatrix = new double[uniqueItems.size()][uniqueItems.size()];
		
		for(int e=0 ; e < uniqueItems.size() ; e++){
			RankedItem ri = uniqueItems.get(e);
			HashMap<String,Double> higherRanked = new HashMap<String, Double>();
			double countTransitions = 0.0;
			for(Ranking r:rankings){
				for(RankedItem it:r.getRankingList()){
					if(it.getScore()>ri.getScore()){
						if(higherRanked.get(it.getName())==null){
							higherRanked.put(it.getName(),1.0);
						}else{
							higherRanked.put(it.getName(),higherRanked.get(it.getName())+1.0);
						}
						countTransitions ++;
					}
				}
			}
			for(int i=0;i<uniqueItems.size();i++){
				transitionMatrix[i][e] = higherRanked.get(uniqueItems.get(i).getName())!=null ? higherRanked.get(uniqueItems.get(i).getName()) /countTransitions : 0.0;
			}
			
		}
		
		return transitionMatrix;
	}

}
