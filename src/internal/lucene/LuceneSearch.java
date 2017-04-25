package internal.lucene;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import metasearch.Searcher;

public class LuceneSearch implements Searcher{
	
   String indexDir = "C:/Users/Usuarioç/npm_data/index";
   String dataDir = "C:/Users/Usuarioç/documents";
//   String indexDir = "C:/npm_data/indexcriptions";
//   String dataDir = "C:/npm_data/descriptions";
   Indexer indexer;
   LuceneSearcher searcher;

   private void createIndex() throws IOException{
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

	public List<String> search(String searchQuery) {
		List<String> results = new ArrayList<String>();
		try {
			searcher = new LuceneSearcher(indexDir);
			long startTime = System.currentTimeMillis();
			TopDocs hits = searcher.search(searchQuery);
			long endTime = System.currentTimeMillis();

			System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				String[] path = doc.get(LuceneConstants.FILE_PATH).split("\\\\");
				String nameDoc = path[path.length - 1];
				nameDoc = nameDoc.replaceAll(".txt", "");
				// System.out.println(nameDoc + " "+ scoreDoc.score);
				results.add(nameDoc);
			}
			searcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public String getName() {
		return "lucene";
	}
}