package test;

import java.io.IOException;
import java.util.List;

import util.HitManager;
import util.ResultsManager;

public class PutHitOrNotQueries {

	public static void main(String[] args) {
		
		int max = 20;
		String[] folders = {"WeightedFirstRankingAgregator","WeightedRankingAgregator","WeightedRankingAgregator701010"};
		
		for (String query : HitManager.getInstance().getQueries()) {
			
			for(int e=0;e<folders.length;e++){
				List<String> results = ResultsManager.getInstance().loadResults(folders[e], query, max);
				for(String pkg:results){
					System.out.println("Hits for \""+query+"\""+" input 1/0 as yes/no" );
					int inChar;
			        System.out.print("pkg ");
			        try {
			            inChar = System.in.read();
			            if(inChar==1){
			            	HitManager.getInstance().addHit(query, pkg);
			            }
			        }
			        catch (IOException ex){
			            System.out.println("Error reading from user");
			        }
				}
			}

		}		

	}

}
