package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import aggregators.SimpleAgregator;
import external.wrappers.BingWrapper;
import external.wrappers.DuckWrapper;
import external.wrappers.GoogleWrapper;
import external.wrappers.NPMWrapper;
import external.wrappers.YARNWrapper;
import internal.lucene.LuceneSearch;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherWithCache;
import metasearch.Searcher;

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
    

public class TestSingleQuery {

	public static void main(String[] args) {
		
		LuceneSearch tester = new LuceneSearch();	
		GoogleWrapper scraper = new GoogleWrapper();
		NPMWrapper npm1 = new NPMWrapper(11, NPMWrapper.OPTIMAL);
		NPMWrapper npm2 = new NPMWrapper(11, NPMWrapper.POPULARITY);
		NPMWrapper npm3 = new NPMWrapper(11, NPMWrapper.QUALITY);
		NPMWrapper npm4 = new NPMWrapper(11, NPMWrapper.MAINTENANCE);
		BingWrapper bing = new BingWrapper();
		//YARNWrapper yarn = new YARNWrapper();
		//DuckWrapper duck = new DuckWrapper();
		ArrayList<Searcher> searchers = new ArrayList<Searcher>();
		searchers.add(tester);
		searchers.add(scraper);
		searchers.add(npm1);
		searchers.add(npm2);
		searchers.add(npm3);
		searchers.add(npm4);
		searchers.add(bing);
		//searchers.add(duck);
		
		Aggregator aggregator = new SimpleAgregator();
		
		MetaSearcher meta = new MetaSearcherWithCache(searchers, aggregator);
		List<String> results = new ArrayList<String>();
		try {
			results = meta.search("user login");
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.println("RESULTADOS");
		for(String tech:results){
				System.out.println(tech);
		}
		
		/*executeQuery("bounded buffer");
		executeQuery("quick sort");
		executeQuery("depth first search");
		executeQuery("regular expression");
		executeQuery("tic tac toe");
		executeQuery("ftp server");
		executeQuery("tcp server");
		executeQuery("rmi server");
		executeQuery("chat server");
		executeQuery("ftp client");*/
		
		

	}

}
