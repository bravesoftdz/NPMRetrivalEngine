package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import aggregators.WeightedFirstRankingAgregator;
import aggregators.WeightedRankingAgregator;
import external.wrappers.BingWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMWrapper;
import internal.lucene.LuceneSearch;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherWithCache;
import metasearch.Searcher;
import ranking.RankedItem;
import ranking.Ranking;
import util.QueryManager;

/*
Q1 bounded buffer 5
Q2 quick sort 10
Q3 depth first search 5
Q4 regular expression 3
Q5 tic tac toe 3
Q6 ftp server 5
Q7 tcp server 10
Q8 rmi server 5
Q9 chat server 7
Q9 ftp client 4
*/

public class TestAllQueries {

	public static void main(String[] args) {

		Proxy proxy = new Proxy( //
				Proxy.Type.HTTP, //
				InetSocketAddress.createUnresolved("proxy.exa.unicen.edu.ar", 8080) //
		);

		LuceneSearch tester = new LuceneSearch(200);

		NPMWrapper npm1 = new NPMWrapper(200, NPMWrapper.OPTIMAL);
		NPMWrapper npm2 = new NPMWrapper(200, NPMWrapper.POPULARITY);
		NPMWrapper npm3 = new NPMWrapper(200, NPMWrapper.QUALITY);
		NPMWrapper npm4 = new NPMWrapper(200, NPMWrapper.MAINTENANCE);

		GoogleWrapper scraper = new GoogleWrapper(50);
		BingWrapper bing = new BingWrapper(50);
		// YARNWrapper yarn = new YARNWrapper();
		// DuckWrapper duck = new DuckWrapper();
		ArrayList<Searcher> searchers = new ArrayList<Searcher>();
		searchers.add(tester);
		searchers.add(scraper);
		searchers.add(npm1);
		// searchers.add(npm2);
		// searchers.add(npm3);
		// searchers.add(npm4);
		searchers.add(bing);
		// searchers.add(duck);

		List<Aggregator> aggregators = new ArrayList<Aggregator>();

		Double[] weights = { 0.25, 0.25, 0.25, 0.25 };
		Aggregator weightedFirstRankingAgregator = new WeightedFirstRankingAgregator(Arrays.asList(weights));
		aggregators.add(weightedFirstRankingAgregator);

		Aggregator weightedRankingAgregator = new WeightedRankingAgregator(Arrays.asList(weights));
		aggregators.add(weightedRankingAgregator);
		
		Double[] weights2 = { 0.70, 0.10, 0.10, 0.10 };
		Aggregator weightedRankingAgregator2 = new WeightedRankingAgregator(Arrays.asList(weights2));
		weightedRankingAgregator2.setName(weightedRankingAgregator2.getName()+"701010");
		aggregators.add(weightedRankingAgregator2);

		for (Aggregator aggregator : aggregators) {
			for (String query : QueryManager.getInstance().getQueries()) {

				MetaSearcher meta = new MetaSearcherWithCache(searchers, aggregator);
				Ranking results = null;
				try {
					results = meta.search(query, proxy);
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}

				System.out.println();
				System.out.println("RESULTADOS");
				for (RankedItem tech : results.getRankingList()) {
					System.out.println(tech.getName() + ":" + tech.getScore());
				}

				results.saveRankingInFile("results/" + aggregator.getName() +"/"+ query + ".txt");
			}
		}
		

	}

}
