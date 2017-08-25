package test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import aggregators.OnlyTheFirst;
import external.wrappers.BingWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMSearchWrapper;
import external.wrappers.NPMWrapper;
import internal.lucene.LuceneSearch;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherOnline;
import metasearch.Searcher;
import ner.StringMatching;
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

public class ExecuteAllSearchers {

	public static void main(String[] args) {

		Proxy proxy = new Proxy( //
				Proxy.Type.HTTP, //
				InetSocketAddress.createUnresolved("proxy.exa.unicen.edu.ar", 8080) //
		);
		
		int max_queries = 10;

		LuceneSearch tester = new LuceneSearch(200);
		NPMWrapper npm1 = new NPMWrapper(20, NPMWrapper.OPTIMAL);
		GoogleWrapper scraper = new GoogleWrapper(20,new StringMatching());
		BingWrapper bing = new BingWrapper(20,new StringMatching());
		NPMSearchWrapper npmsearch = new NPMSearchWrapper(20);

		
		ArrayList<Searcher> lucene = new ArrayList<Searcher>();
		lucene.add(tester);
		ArrayList<Searcher> google = new ArrayList<Searcher>();
		google.add(scraper);
		ArrayList<Searcher> npm = new ArrayList<Searcher>();
		npm.add(npm1);
		ArrayList<Searcher> bings = new ArrayList<Searcher>();
		bings.add(bing);
		ArrayList<Searcher> npmsearchers = new ArrayList<Searcher>();
		npmsearchers.add(npmsearch);
		
		List<List<Searcher>> searchers = new ArrayList<List<Searcher>>();
		searchers.add(lucene);
		//searchers.add(google);
		//searchers.add(npm);
		//searchers.add(bings);
		//searchers.add(npmsearchers);


		for (List<Searcher> searcher : searchers) {
			
			System.out.println("Analizando "+ searcher.get(0).getName());
			
			for (int i = 0 ; i < max_queries ; i++) {

				String query = QueryManager.getInstance().getQueries().get(i);

				Aggregator aggregator = new OnlyTheFirst();
				aggregator.setName(searcher.get(0).getName());
				MetaSearcher meta = new MetaSearcherOnline(searcher, aggregator);
				Ranking results = null;

				System.out.println("Query "+ query);
				try {
					results = meta.search(query, proxy);
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}


				String path = "results/" + aggregator.getName();
				File directory = new File(path);
				directory.mkdirs();
				results.saveRankingInFile("results/" + aggregator.getName() +"/"+ query + ".txt");
				
				
				for (RankedItem tech : results.getRankingList().subList(0, 19)) {
					System.out.println(tech.getName() + ":" + tech.getScore());
				}
				

				System.out.println();
			}
		}
		

	}

}
