package internal.lucene;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import metasearch.Searcher;
import ranking.RankedItem;
import ranking.Ranking;

public class LuceneSearch implements Searcher {

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

	public Ranking search(String searchQuery, Proxy proxy) {
		List<RankedItem> results = new ArrayList<RankedItem>();
		try {
			//createIndex();
			searcher = new LuceneSearcher(indexDir);
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.search(searchQuery, max_results);
			long endTime = System.currentTimeMillis();

			System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
			for (int i=0;i<hits.scoreDocs.length;i++){
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.getDocument(scoreDoc);
				String[] path = doc.get(LuceneConstants.FILE_PATH).split("\\\\");
				String nameDoc = path[path.length - 1];
				nameDoc = nameDoc.replaceAll(".txt", "");
				// System.out.println(nameDoc + " "+ scoreDoc.score);
				results.add(new RankedItem(nameDoc, (double) (max_results-i)));
			}
			searcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Ranking(results);
	}

	@Override
	public String getName() {
		return "lucene";
	}
}