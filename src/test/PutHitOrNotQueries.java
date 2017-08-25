package test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import ranking.RankedItem;
import ranking.Ranking;
import util.HitManager;

public class PutHitOrNotQueries {

	public static void main(String[] args) {

		int max_position = 20;
		int max_queries = 10;
		
		String[] folders = { "WeightedFirstRankingAgregator", "WeightedRankingAgregator",
				"WeightedRankingAgregator701010", "lucene", "bing.com", "google.com_str_match", "npmjs.comoptimal20", "npmsearch.com" };
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
				String filepath = "results/"+folders[e]+"/"+query+".txt";
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

		System.out.println("Do you want to rewrite the csv file? y/n");
		try {
			Scanner sc = new Scanner(System.in);
			if (sc.nextLine().equals("y")) {
				saveToCSV(folders, hits4Search);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			end = true;
		}
		
		System.out.println("Do you want to save statistics in csv? y/n");
		try {
			Scanner sc = new Scanner(System.in);
			if (sc.nextLine().equals("y")) {
				saveStatisticsToCSV(folders,rankingsXqueries,hitsXqueries);
			}
			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			end = true;
		}

	}

	private static void printSatistics(HashMap<String, List<Ranking>> rankingsXqueries, HashMap<String, List<String>> hitsXqueries) {
		System.out.println("--------------Statistics--------------");
		System.out.println();
		for(String query:rankingsXqueries.keySet()){
			System.out.println("Query: "+query);
			System.out.println();
			for(Ranking ranking:rankingsXqueries.get(query)){
				System.out.println("Name: "+ranking.getId());
				System.out.println("K-Presition: "+ranking.getKPrecisionList(20));
				System.out.println("K-Recall: "+ranking.getKRecallList(20,hitsXqueries.get(query).size()));
				System.out.println("K-FMeasure: "+ranking.getKFMeasureList(20,hitsXqueries.get(query).size()));
			}
			System.out.println();
		}
		System.out.println("----------End of Statistics-----------");
		System.out.println();
		
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
	
	public static void saveStatisticsToCSV(String[] folders, HashMap<String, List<Ranking>> rankingsXqueries, 
			HashMap<String, List<String>> hitsXqueries) {
	
		String fichero = "csv/statistics.csv";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(fichero);
			pw = new PrintWriter(fw);
			pw.print("query");
			for (int e = 0; e < folders.length; e++) {
				pw.print("," + folders[e]);
			}
			pw.println();
			
			for(String query:rankingsXqueries.keySet()){
				pw.print(query);
				for(Ranking ranking:rankingsXqueries.get(query)){
					pw.print(","+saveAndGetKStatsFile(ranking,20,hitsXqueries.get(query).size()));
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
	
	private static String saveAndGetKStatsFile(Ranking ranking, int k, int goldenSize) {
		String fichero = "csv/statistics/"+ranking.getId()+".csv";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			if(ranking.getId()!=null){
				fw = new FileWriter(fichero);
				pw = new PrintWriter(fw);
				pw.print("k,hits,precision,recall,fmeasure");
				pw.println();
				
				List<Double> kprecision = ranking.getKPrecisionList(k);
				List<Double> krecall = ranking.getKRecallList(k, goldenSize);
				List<Double> kfmeasure = ranking.getKFMeasureList(k, goldenSize);
				List<Double> khits = ranking.getKHitList(k);
					
				for(int i = 0; i<k ;i++){
					pw.print((i+1)+","+khits.get(i)+","+kprecision.get(i)+","+krecall.get(i)+","+kfmeasure.get(i));
					pw.println();
				}
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
		return fichero;
	}

	private static String toCsvList(List<Double> l){
		String r = "";
		for(Double i:l){
			r = r + i.toString() + " ";
		}
		return r.substring(0, r.lastIndexOf(" ")-1);
	}

}
