package task;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aggregators.Aggregator;
import aggregators.BoostedBordaFuse;
import aggregators.BordaFuse;
import aggregators.CordorcetAggregator;
import aggregators.FilterExternalRankAgregator;
import aggregators.M1;
import aggregators.M2;
import aggregators.M3;
import aggregators.M4;
import aggregators.WeightedBordaFuse;
import aggregators.WeightedFirstRankingAgregator;
import external.wrappers.BingWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMSearchWrapper;
import external.wrappers.NPMWrapper;
import internal.lucene.LuceneSearch;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherImp;
import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ner.HiperlinkMatching;
import ranking.RankedItem;
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
 * @author Usuario�
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
		//BingWrapper bing = new BingWrapper(max_results,new HiperlinkMatching());
		NPMSearchWrapper npmsearch = new NPMSearchWrapper(max_results);
		
		/**
		 * Internal Searchers
		 * 
		 */
		
		LuceneSearch lucene = new LuceneSearch(max_results,proxy);
		
		
		/**
		 * Artificial Searcher
		 */
		/*Double[] weights = { 0.25, 0.25, 0.25, 0.25 };
		WeightedFirstRankingAgregator weightedFirstRankingAgregator = new WeightedFirstRankingAgregator(Arrays.asList(weights));
		List<Searcher> silver = new ArrayList<Searcher>();
		silver.add(lucene);
		silver.add(google);
		silver.add(npm);
		for (int i = 0 ; i < max_queries ; i++) {

			String query = QueryManager.getInstance().getQueries().get(i);

			System.out.println("Query "+ query);
			
			MetaSearcher meta = new MetaSearcherImp(silver, weightedFirstRankingAgregator, max_results);
			
			Ranking ranking = null; //CacheRankingManager.getInstance().loadRankingFromCache(meta, query);
			if (ranking == null) {
				meta.acquireData(query, proxy);
				ranking = meta.processData(null);//TODO data null
				CacheRankingManager.getInstance().saveRankingInCache(ranking, meta, query);
			}
			
			System.out.println();

		}*/
		
		
		List<Searcher> filters = new ArrayList<Searcher>();
		filters.add(lucene);
		filters.add(npm);	
		List<Searcher> externalRank = new ArrayList<Searcher>();
		externalRank.add(google);
		
		FilterExternalRankAgregator filterGoogleRankAgregator = new FilterExternalRankAgregator();
		filterGoogleRankAgregator.setName(filterGoogleRankAgregator.getPrefix()+externalRank.get(0).getId());

		/*for (int i = 0 ; i < max_queries ; i++) {

			String query = QueryManager.getInstance().getQueries().get(i);

			System.out.println("Query "+ query);
			
			List<Ranking> rank_filters = new ArrayList<Ranking>();
			for(Searcher filter:filters){
				Ranking r = CacheRankingManager.getInstance().loadRankingFromCache(filter, query)!=null?
						CacheRankingManager.getInstance().loadRankingFromCache(filter, query):
							new Ranking(new ArrayList<RankedItem>());;
				rank_filters.add(r);
			}
			
			filterGoogleRankAgregator.setFilters(rank_filters);
			
			MetaSearcher meta = new MetaSearcherImp(externalRank, filterGoogleRankAgregator, max_results);
			
			Ranking ranking = null; //CacheRankingManager.getInstance().loadRankingFromCache(meta, query);
			if (ranking == null) {
				meta.acquireData(query, proxy);
				ranking = meta.processData(null);//TODO data null
				CacheRankingManager.getInstance().saveRankingInCache(ranking, meta, query);
			}
			
			System.out.println();

		}
		*/
		
		
		/**
		 * Aggregators
		 */
		
		List<Searcher> searchers = new ArrayList<Searcher>();
		//searchers.add(lucene);
		//searchers.add(google);
		searchers.add(npm);
		searchers.add(npmsearch);
		searchers.add(new MetaSearcherImp(externalRank, filterGoogleRankAgregator, max_results));
		
		List<Aggregator> aggregators = new ArrayList<Aggregator>();

		

		/*Aggregator m1 = new M1();
		aggregators.add(m1);
		
		Aggregator m2 = new M2();
		aggregators.add(m2);
		
		Aggregator m3 = new M3();
		aggregators.add(m3);
		
		Aggregator m4 = new M4();
		aggregators.add(m4);*/
		
		Double[] weights_borda = {0.50, 0.00, 0.50};
		Aggregator w_borda = new WeightedBordaFuse(Arrays.asList(weights_borda));
		aggregators.add(w_borda);
		
		/*Aggregator borda = new BordaFuse();
		aggregators.add(borda);
		
		Aggregator boosted_borda = new BoostedBordaFuse();
		aggregators.add(boosted_borda);
		
		Aggregator cordorcet = new CordorcetAggregator();
		aggregators.add(cordorcet);*/
		
		for (Aggregator aggregator : aggregators) {
			for (int i = 0 ; i < max_queries ; i++) {

				String query = QueryManager.getInstance().getQueries().get(i);

				System.out.println("Query "+ query);
				
				MetaSearcher meta = new MetaSearcherImp(searchers, aggregator, max_results);
				
				Ranking ranking = null/*CacheRankingManager.getInstance().loadRankingFromCache(meta, query)*/;
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
