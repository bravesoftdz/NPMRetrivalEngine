package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class Ranking implements Serializable {

	private static final long serialVersionUID = 1L;

	protected List<RankedItem> rankingList;
	protected List<Double> precisionAcum;
	protected List<Double> recallAcum;
	protected List<Double> fMeasureAcum;

	public Ranking(List<RankedItem> results) {
		super();
		this.rankingList = results;
		sort();
	}

	public Ranking(String filename) {
		super();
		this.rankingList = loadRankingFromFile(filename);
		sort();
	}

	public List<RankedItem> getRankingList() {
		return rankingList;
	}

	public int size() {
		return rankingList.size();
	}

	public void setRankingList(ArrayList<RankedItem> rankingList) {
		this.rankingList = rankingList;
	}

	public boolean contains(RankedItem item) {
		return rankingList.contains(item);
	}

	public void sort() {
		rankingList.sort(new Comparator<RankedItem>() {
			@Override
			public int compare(RankedItem r1, RankedItem r2) {
				return r1.compareTo(r2) * -1;
			}
		});
	}

	/**
	 * This method need an Order Ranking
	 * 
	 * @return
	 */
	public ArrayList<Double> getPrecisionAcumList() {

		ArrayList<Double> precisionList = new ArrayList<Double>();

		int sumVerified = 0;
		for (int i = 0; i < rankingList.size(); i++) {
			if (rankingList.get(i).isHit()) {
				sumVerified++;
			}
			if (sumVerified != 0) {
				precisionList.add((double) sumVerified / (double) (i + 1));
			} else {
				precisionList.add(0.0);
			}

		}

		this.precisionAcum = precisionList;

		return precisionList;
	}

	public Double getMaxPrecision() {
		if (precisionAcum == null) {
			precisionAcum = getPrecisionAcumList();
		}
		Double max = 0.0;
		for (Double d : precisionAcum) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	public ArrayList<Double> getRecallAcumList(int goldenSize) {

		ArrayList<Double> recallList = new ArrayList<Double>();

		int sumVerified = 0;
		for (int i = 0; i < rankingList.size(); i++) {
			if (rankingList.get(i).isHit()) {
				sumVerified++;
			}
			recallList.add((double) sumVerified / (double) goldenSize);

		}

		this.recallAcum = recallList;

		return recallList;
	}

	public Double getMaxRecall(int goldenSize) {
		if (recallAcum == null) {
			recallAcum = getRecallAcumList(goldenSize);
		}
		Double max = 0.0;
		for (Double d : recallAcum) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	// =2*((J4*K4)/(J4+K4))
	public ArrayList<Double> getFMeasureAcumList(int goldenSize) {

		if (precisionAcum == null) {
			precisionAcum = getPrecisionAcumList();
		}

		if (recallAcum == null) {
			recallAcum = getRecallAcumList(goldenSize);
		}

		ArrayList<Double> fMeasureList = new ArrayList<Double>();

		for (int i = 0; i < rankingList.size(); i++) {
			if (precisionAcum.get(i).doubleValue() + recallAcum.get(i).doubleValue() != 0) {
				fMeasureList.add(
						(double) ((double) 2 * ((precisionAcum.get(i).doubleValue() * recallAcum.get(i).doubleValue())
								/ (precisionAcum.get(i).doubleValue() + recallAcum.get(i).doubleValue()))));
			} else {
				fMeasureList.add(0.0);
			}

		}

		this.fMeasureAcum = fMeasureList;

		return fMeasureList;
	}

	public Double getMaxFMeasure(int goldenSize) {
		if (fMeasureAcum == null) {
			fMeasureAcum = getFMeasureAcumList(goldenSize);
		}
		Double max = 0.0;
		for (Double d : fMeasureAcum) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	public void saveRankingInFile(String fileName) {
		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter(fileName);
			pw = new PrintWriter(fichero);

			for (RankedItem item : rankingList) {
				pw.println(item.getName() + ":" + item.getScore());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	public List<RankedItem> loadRankingFromFile(String fileName) {

		List<RankedItem> results = new ArrayList<RankedItem>();
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(fileName);

			br = new BufferedReader(fr);
			String linea;
			
			while ((linea = br.readLine()) != null) {
				String[] nameAndScore = linea.split(":");
				if (nameAndScore.length == 2) {
					results.add(new RankedItem(nameAndScore[0], Double.valueOf(nameAndScore[1])));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fr!=null){
					fr.close();	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		return results;
	}

	public Double calculateSpearmanCorrelation() {
		double[] rank = new double[rankingList.size()];
		double[] golden = new double[rankingList.size()];
		this.fixRank(golden, rank);
		for (int i = 0; i < rankingList.size(); i++) {
			RankedItem item = rankingList.get(i);
			rank[i] = item.getScore();
			golden[i] = (item.isHit()) ? 1 : 0;
		}
		double spearman = new SpearmansCorrelation().correlation(golden, rank);

		return spearman;
	}

	public Double calculatePearsonsCorrelation() {
		double[] rank = new double[rankingList.size()];
		double[] golden = new double[rankingList.size()];
		this.fixRank(golden, rank);

		for (int i = 0; i < rankingList.size(); i++) {
			RankedItem item = rankingList.get(i);
			rank[i] = item.getScore();
			golden[i] = (item.isHit()) ? 1 : 0;
		}

		PearsonsCorrelation pearsons = new PearsonsCorrelation();

		return pearsons.correlation(golden, rank);
	}

	public Double calculateKendallsCorrelation() {
		double[] rank = new double[rankingList.size()];
		double[] golden = new double[rankingList.size()];
		this.fixRank(golden, rank);

		for (int i = 0; i < rankingList.size(); i++) {
			RankedItem item = rankingList.get(i);
			rank[i] = item.getScore();
			golden[i] = (item.isHit()) ? 1 : 0;
		}
		double kendalls = new KendallsCorrelation().correlation(golden, rank);

		return kendalls;
	}

	private int countVerified(double[] golden) {
		int sum = 0;
		for (double i : golden) {
			if (i == 1.0) {
				sum++;
			}
		}
		return sum;
	}

	private void fixRank(double[] golden, double[] rank) {
		int countVerified = countVerified(golden);
		for (int i = 0; i < rank.length; i++) {
			if (i < countVerified) {
				rank[i] = 1.0;
			} else {
				rank[i] = 0.0;
			}
		}
	}

}
