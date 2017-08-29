package internal.lucene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jsoup.Jsoup;

import metasearch.Searcher;
import metasearch.cache.CacheRankingManager;
import ner.EntityExtractor;
import ranking.RankedItem;
import ranking.Ranking;
import util.ConfigManager;

public class LuceneSearch implements Searcher {
	
	private static final String datasetURL = ConfigManager.getInstance().getProperty("dataset_url");
	private static final String download_path = ConfigManager.getInstance().getProperty("download_path");

	String indexDir = "C:/Users/Usuarioç/npm_data/index";
	String dataDir = "C:/Users/Usuarioç/documents";
	// String indexDir = "C:/npm_data/indexcriptions";
	// String dataDir = "C:/npm_data/descriptions";
	Indexer indexer;
	LuceneSearcher searcher;

	int max_results = 200;

	public LuceneSearch(int max_results) {
		this.max_results = max_results;
	}

	private void createIndex() throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
	}

	public Ranking search(String query, Proxy proxy) {

		Ranking r = CacheRankingManager.getInstance().loadRankingFromCache(this, query);

		if (r != null) {
			return r;
		} else {
			
			//acquireData(query, proxy);
			
			// createIndex();
			
			List<String> queries= new ArrayList<String>();
			queries.add(query);
			
			r = processData(queries,null);
			
			CacheRankingManager.getInstance().saveRankingInCache(r, this, query);
		}
		return r;
	}

	@Override
	public String getName() {
		return "lucene";
	}
	
	@Override
	public Ranking processData(List<String> contents, EntityExtractor ent_extractor) {
		List<RankedItem> results = new ArrayList<RankedItem>();
		try {
			
			searcher = new LuceneSearcher(indexDir);
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.search(contents.get(0), max_results);
			long endTime = System.currentTimeMillis();

			System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.getDocument(scoreDoc);
				String[] path = doc.get(LuceneConstants.FILE_PATH).split("\\\\");
				String nameDoc = path[path.length - 1];
				nameDoc = nameDoc.replaceAll(".txt", "");
				// System.out.println(nameDoc + " "+ scoreDoc.score);
				results.add(new RankedItem(nameDoc, (double) (max_results - i)));
			}
			searcher.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return new Ranking(results);
	}

	@Override
	public List<String> acquireData(String query, Proxy proxy) {

		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		try {
			URL url = new URL(datasetURL);
			// read this file into InputStream
			inputStream = url.openConnection(proxy).getInputStream();

			// write the inputStream to a FileOutputStream
			if (proxy != null) {
				inputStream = url.openConnection(proxy).getInputStream();
			} else {
				inputStream = url.openStream();
			}
			
			outputStream = 
	                    new FileOutputStream(new File(download_path));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		
		return null;
		 
	}
}