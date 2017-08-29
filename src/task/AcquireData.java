package task;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import external.wrappers.BingWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMSearchWrapper;
import external.wrappers.NPMWrapper;
import metasearch.Searcher;
import metasearch.cache.CacheContentManager;
import ner.StringMatching;
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
 * Tarea para obtener datos desde fuentes de internet o desde la cache de contenidos
 *  
 * @author Usuario�
 *
 */

public class AcquireData {

	public static void main(String[] args) {

		Proxy proxy = new Proxy( //
				Proxy.Type.HTTP, //
				InetSocketAddress.createUnresolved("proxy.exa.unicen.edu.ar", 8080) //
		);
		
		int max_queries = Integer.valueOf(ConfigManager.getInstance().getProperty("max_queries"));
		int max_results = Integer.valueOf(ConfigManager.getInstance().getProperty("max_results"));

		//LuceneSearch lucene = new LuceneSearch(200);
		NPMWrapper npm = new NPMWrapper(max_results, NPMWrapper.OPTIMAL);
		GoogleWrapper google = new GoogleWrapper(max_results,new StringMatching());
		BingWrapper bing = new BingWrapper(max_results,new StringMatching());
		NPMSearchWrapper npmsearch = new NPMSearchWrapper(max_results);
		
		List<Searcher> searchers = new ArrayList<Searcher>();
		//searchers.add(lucene);
		searchers.add(google);
		searchers.add(npm);
		searchers.add(bing);
		searchers.add(npmsearch);


		for (Searcher searcher : searchers) {
			
			System.out.println("Analizando "+ searcher.getName());
			
			for (int i = 0 ; i < max_queries ; i++) {

				String query = QueryManager.getInstance().getQueries().get(i);

				System.out.println("Query "+ query);
				
		        List<String> data = CacheContentManager.getInstance().loadContentFromCache(searcher,query);
		        if(data==null){
		        	data = searcher.acquireData(query,proxy);
		        }
				
				CacheContentManager.getInstance().saveContentInCache(data,searcher,query);

				System.out.println();
			}
		}
		

	}

}
