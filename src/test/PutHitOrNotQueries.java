package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.print.attribute.HashAttributeSet;

import util.HitManager;
import util.ResultsManager;

public class PutHitOrNotQueries {

	public static void main(String[] args) {

		int max = 20;
		String[] folders = { "WeightedFirstRankingAgregator", "WeightedRankingAgregator",
				"WeightedRankingAgregator701010", "lucene", "bing.com", "google.com", "npmjs.comoptimal20", "npmsearch.com" };
		boolean end = false;
		HashMap<String, Integer> hits4Search = new HashMap<String, Integer>();

		int q = 0;
		for (String query : HitManager.getInstance().getQueries()) {
			for (int e = 0; e < folders.length; e++) {
				List<String> results = ResultsManager.getInstance().loadResults(folders[e], query, max);
				System.out.println(
						"Hits for " + folders[e] + " q:" + q + " \"" + query + "\"" + " input y=yes, n=no, e=end");
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

					if (HitManager.getInstance().isAHit(query, pkg)) {
						hits++;
					}
				}
				System.out.println("Hits " + hits + "/" + max);
				hits4Search.put(folders[e] + query, hits);
				if (end) {
					break;
				}
			}
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

		System.out.println("Do you want to rewrite the csv file? y/n");
		try {
			Scanner sc = new Scanner(System.in);
			if (sc.nextLine().equals("y")) {
				saveToCSV(folders, hits4Search);
			}
			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			end = true;
		}

	}

	public static void saveToCSV(String[] folders, HashMap<String, Integer> hits4Search) {
		String fichero = "csv/hits4Search2.csv";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(fichero);
			pw = new PrintWriter(fw);
			pw.print("query");
			for (int e = 0; e < folders.length; e++) {
				pw.print("," + folders[e]);
			}
			for (String query : HitManager.getInstance().getQueries()) {
				pw.print(query);
				for (int e = 0; e < folders.length; e++) {
					pw.print("," + hits4Search.get(folders[e] + query));
				}
				pw.println();
			}
		} catch (Exception e) {
			pw.println("Excepcion leyendo fichero " + fichero + ": " + e.getMessage());
		} finally {
			try {
				if (null != fw)
					fw.close();
			} catch (Exception e2) {
				pw.println("Excepcion cerrando fichero " + fichero + ": " + e2.getMessage());
			}
		}
	}

}
