package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.queryParser.ParseException;

import aggregators.Aggregator;
import aggregators.WeightedFirstRankingAgregator;
import internal.lucene.LuceneSearch;
import metasearch.MetaSearcher;
import metasearch.MetaSearcherOnline;
import metasearch.Searcher;
import ranking.RankedItem;
import ranking.Ranking;
  

public class TestLucene {

	public static void main(String[] args) {
		
		String dataDir = "C:/npm_data/descriptions";
		
		Proxy proxy = new Proxy(                                      //
			    Proxy.Type.HTTP,                                      //
			    InetSocketAddress.createUnresolved("proxy.exa.unicen.edu.ar", 8080) //
			);
		
		LuceneSearch tester = new LuceneSearch(3000);
		ArrayList<Searcher> searchers = new ArrayList<Searcher>();
		searchers.add(tester);
		
		Double[] weights = {0.25,0.25,0.25,0.25};
		Aggregator aggregator = new WeightedFirstRankingAgregator(Arrays.asList(weights));
		
		
		String query = "coveralls.io/repos/github";
		//String query = "img.shields.io/badge/coverage";
		
		MetaSearcher meta = new MetaSearcherOnline(searchers, aggregator);
		Ranking results = null;
		try {
			results = meta.search(query, proxy);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.println("RESULTADOS");
		for(RankedItem tech:results.getRankingList()){
				System.out.println("<a href=\""+"https://www.npmjs.com/package/"+tech.getName()+"\">"+tech.getName()+"</a>");
		}
		
		//results.saveRankingInFile("results/"+query+".txt");		
		

	}
}
