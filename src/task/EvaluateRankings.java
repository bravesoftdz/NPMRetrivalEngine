package task;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import ranking.RankedItem;
import ranking.Ranking;
import util.ConfigManager;
import util.HitManager;

public class EvaluateRankings {
	
	public static final String RESULTS = ConfigManager.getInstance().getProperty("ranking_folder");
	public static final String EXT = ".txt";

	public static void main(String[] args) {

		int max_queries = Integer.valueOf(ConfigManager.getInstance().getProperty("max_queries"));
		int max_position = Integer.valueOf(ConfigManager.getInstance().getProperty("top_results"));
		
		/*String[] folders = { "lucene", "bing.com_stanford_CRF", "google.com_stanford_CRF", "bing.com_str_match", "google.com_str_match", "bing.com_hyp_match", "google.com_hyp_match", "npmjs.com_optimal", "npmsearch.com", 
				"M1","M2","M3","M4","BordaFuse","Cordorcet","BoostedBordaFuse","WeightedFirstRankingAgregator"};*/
		String[] folders = { "npmjs.com_optimal", "npmsearch.com", 
				"M1","M2","M3","M4","BordaFuse","Cordorcet","BoostedBordaFuse","WeightedFirstRankingAgregator"};

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

				int hits = 0;
				for (int i = 0; i < (ranking.getRankingList().size()<max_position?ranking.getRankingList().size():max_position); i++) {
					RankedItem item = ranking.getRankingList().get(i);
					String pkg = item.getName();

					if (!HitManager.getInstance().isAHit(query, pkg)
							&& !HitManager.getInstance().isNotAHit(query, pkg)) {
						
						System.out.println("WARNING: For item "+pkg+" - It is not possible to know if it is hit or not");
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
				pw.print("k,#hits,precision,recall,fmeasure,hit");
				pw.println();
				
				List<Double> kprecision = ranking.getKPrecisionList(k);
				List<Double> krecall = ranking.getKRecallList(k, goldenSize);
				List<Double> kfmeasure = ranking.getKFMeasureList(k, goldenSize);
				List<Double> khits = ranking.getKHitList(k);
					
				for(int i = 0; i<k ;i++){
					int hit = 0;
					if(i<ranking.getRankingList().size()){
						hit = ranking.getRankingList().get(i).isHit()?1:0;
					}
					pw.print((i+1)+","+khits.get(i)+","+kprecision.get(i)+","+krecall.get(i)+","+kfmeasure.get(i)+","+hit);
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
