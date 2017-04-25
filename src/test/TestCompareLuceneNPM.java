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
    

public class TestCompareLuceneNPM {

	public static void main(String[] args) {
		
		
		NPMWrapper npm1 = new NPMWrapper(22, NPMWrapper.OPTIMAL);
		//NPMWrapper npm2 = new NPMWrapper(11, NPMWrapper.POPULARITY);
		//NPMWrapper npm3 = new NPMWrapper(11, NPMWrapper.QUALITY);
		//NPMWrapper npm4 = new NPMWrapper(11, NPMWrapper.MAINTENANCE);
		
		System.out.println();
		String query = "user login";
		System.out.println("Executing Query: " + query);
		
		LuceneSearch tester = new LuceneSearch();
		List<String> resultRetrival = tester.search(query);
		List<String> resultNPM = npm1.search(query);
		
		System.out.println("Resultados");
		System.out.println("Solo Lucene");
		for(String tech:resultRetrival){
			if(!resultNPM.contains(tech)){
				System.out.println(tech);
			}
		}
		System.out.println("Solo NPM");
		for(String tech:resultNPM){
			if(!resultRetrival.contains(tech)){
				System.out.println(tech);
			}
		}
		System.out.println("Compartidos");
		for(String tech:resultRetrival){
			if(resultNPM.contains(tech)){
				System.out.println(tech);
			}
		}
		System.out.println();
		

	}

}
