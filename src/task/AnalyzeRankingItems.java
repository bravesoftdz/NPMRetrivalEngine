package task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import ranking.RankedItem;
import ranking.Ranking;
import util.ConfigManager;
import util.HitManager;

/**
 * Tarea de analizar si un elemento del ranking es un hit o no
 * 
 * @author Usuarioç
 *
 */

public class AnalyzeRankingItems {
	
	public static final String RESULTS = ConfigManager.getInstance().getProperty("ranking_folder");
	public static final String EXT = ".txt";

	public static void main(String[] args) {
		
		int max_queries = Integer.valueOf(ConfigManager.getInstance().getProperty("max_queries"));
		int max_position = Integer.valueOf(ConfigManager.getInstance().getProperty("max_results"));
		
		String[] folders = { "WeightedFirstRankingAgregator", "WeightedRankingAgregator",
				"lucene", "bing.com", "google.com_str_match", "npmjs.comoptimal", "npmsearch.com" };
		boolean end = false;
		HashMap<String, Integer> hits4Search = new HashMap<String, Integer>();
		
		HashMap<String,List<Ranking>> rankingsXqueries = new HashMap<String,List<Ranking>>();
		HashMap<String,List<String>> hitsXqueries = new HashMap<String,List<String>>();

		int q = 0;
		for (int qnum = 0; qnum < max_queries ; qnum++) {
			String query =  HitManager.getInstance().getQueries().get(qnum);
			List<Ranking> rankings = new ArrayList<Ranking>();
			List<String> pkghits = new ArrayList<String>();
			for (int e = 0; e < folders.length; e++) {		
				String filepath = RESULTS + "/" + folders[e] + "/" + query + EXT;
				Ranking ranking = new Ranking(folders[e]+"_"+query,filepath);
				rankings.add(ranking);
				
				System.out.println(
						"Hits for " + folders[e] + " q:" + q + " \"" + query + "\"" + " input y=yes, n=no, e=end");
				int hits = 0;
				for (int i = 0; i < (ranking.getRankingList().size()<max_position?ranking.getRankingList().size():max_position); i++) {
					RankedItem item = ranking.getRankingList().get(i);
					String pkg = item.getName();

					if (!HitManager.getInstance().isAHit(query, pkg)
							&& !HitManager.getInstance().isNotAHit(query, pkg)) {

						String inChar = "";
						try {
							System.out.print(i + " - " + pkg);
							Scanner sc = new Scanner(System.in);
							inChar = sc.nextLine();
							if (inChar.equals("y")) {
								HitManager.getInstance().addHit(query, pkg);
							}
							if (inChar.equals("n")) {
								HitManager.getInstance().addNOHit(query, pkg);
							}
							if (inChar.equals("e")) {
								end = true;
								break;
							}
						} catch (Exception ex) {
							System.out.println("Error reading from user: " + ex.getMessage());
							ex.printStackTrace();
						}
					}

					if (HitManager.getInstance().isAHit(query, pkg)) {
						item.setHit(true);
						hits++;
						if(!pkghits.contains(pkg)){
							pkghits.add(pkg);
						}
					}else{
						item.setHit(false);
					}
				}
				System.out.println("Hits " + hits + "/" + max_position);
				hits4Search.put(folders[e] + query, hits);
				if (end) {
					break;
				}
			}
			
			rankingsXqueries.put(query, rankings);
			hitsXqueries.put(query, pkghits);
			
			if (end) {
				break;
			}
			q++;

		}

		System.out.println("Do you want to save? y/n");
		try {
			Scanner sc = new Scanner(System.in);
			if (sc.nextLine().equals("y")) {
				HitManager.getInstance().saveQueriesXhits();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			end = true;
		}
	}

}
