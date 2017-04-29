package test;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import util.HitManager;
import util.ResultsManager;

public class PutHitOrNotQueries {

	public static void main(String[] args) {

		int max = 20;
		String[] folders = { "WeightedFirstRankingAgregator", "WeightedRankingAgregator",
				"WeightedRankingAgregator701010" };
		boolean end = false;

		for (String query : HitManager.getInstance().getQueries()) {
			for (int e = 0; e < folders.length; e++) {
				List<String> results = ResultsManager.getInstance().loadResults(folders[e], query, max);
				System.out.println("Hits for "+folders[e]+" \"" + query + "\"" + " input y=yes, n=no, e=end");
				int hits = 0;
				for (int i = 0; i < results.size(); i++) {
					String pkg = results.get(i);

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
					
					if(HitManager.getInstance().isAHit(query, pkg)){
						hits++;
					}
				}
				System.out.println("Hits "+hits+"/"+max);
				if (end) {
					break;
				}
			}
			if (end) {
				break;
			}

		}

		System.out.println("Do you want to save? y/n");
		try {
			Scanner sc = new Scanner(System.in);
			if (sc.nextLine().equals("y")) {
				HitManager.getInstance().saveQueriesXhits();
			}
			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			end = true;
		}

	}

}
