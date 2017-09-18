package task;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aggregators.Aggregator;
import aggregators.M1;
import aggregators.WeightedFirstRankingAgregator;
import external.wrappers.BingWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMSearchWrapper;
import external.wrappers.NPMWrapper;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherImp;
import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ner.HiperlinkMatching;
import ranking.Ranking;
import util.ConfigManager;
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

/**
 * Procesa los datos para obtener rankings
 * 
 * @author Usuarioç
 *
 */

public class AggregateData {

	public static void main(String[] args) {

		Proxy proxy = new Proxy( //
				Proxy.Type.HTTP, //
				InetSocketAddress.createUnresolved("proxy.exa.unicen.edu.ar", 8080) //
		);
		
		int max_queries = Integer.valueOf(ConfigManager.getInstance().getProperty("max_queries"));
		int max_results = 20;//Integer.valueOf(ConfigManager.getInstance().getProperty("max_results"));
		
		
		/**
		 * External searchers
		 */

		NPMWrapper npm = new NPMWrapper(max_results, NPMWrapper.OPTIMAL);
		GoogleWrapper google = new GoogleWrapper(max_results,new HiperlinkMatching());
		BingWrapper bing = new BingWrapper(max_results,new HiperlinkMatching());
		NPMSearchWrapper npmsearch = new NPMSearchWrapper(max_results);
		
		List<Searcher> searchers = new ArrayList<Searcher>();
		searchers.add(google);
		searchers.add(npm);
		searchers.add(bing);
		searchers.add(npmsearch);		
		
		/**
		 * Aggregators
		 */
		List<Aggregator> aggregators = new ArrayList<Aggregator>();

		Double[] weights = { 0.25, 0.25, 0.25, 0.25 };
		Aggregator weightedFirstRankingAgregator = new WeightedFirstRankingAgregator(Arrays.asList(weights));
		aggregators.add(weightedFirstRankingAgregator);

		/*Aggregator weightedRankingAgregator = new WeightedRankingAgregator(Arrays.asList(weights));
		aggregators.add(weightedRankingAgregator);*/
		
		Aggregator m1 = new M1();
		aggregators.add(m1);

		for (Aggregator aggregator : aggregators) {
			for (int i = 0 ; i < max_queries ; i++) {

				String query = QueryManager.getInstance().getQueries().get(i);

				System.out.println("Query "+ query);
				
				MetaSearcher meta = new MetaSearcherImp(searchers, aggregator, max_results);
				
				Ranking ranking = CacheRankingManager.getInstance().loadRankingFromCache(meta, query);
				if (ranking == null) {
					meta.acquireData(query, proxy);
					ranking = meta.processData(null);//TODO data null
					CacheRankingManager.getInstance().saveRankingInCache(ranking, meta, query);
				}
				
				System.out.println();

			}
		}
		

	}

}
