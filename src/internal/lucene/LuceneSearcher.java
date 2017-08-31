package internal.lucene;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneSearcher {
	
   IndexSearcher indexSearcher;
   MultiFieldQueryParser queryParser;
   Query query;
   
   public LuceneSearcher(String indexDirectoryPath) 
      throws IOException{
	   
	  Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
	  IndexReader reader = DirectoryReader.open(dir);
	  indexSearcher = new IndexSearcher(reader);
	    

      queryParser = new MultiFieldQueryParser(new String[]{LuceneConstants.ID, LuceneConstants.DESCR, LuceneConstants.README},
    		  new StandardAnalyzer());
   }
   
   public TopDocs search( String searchQuery, int results) 
      throws IOException, ParseException{
      query = queryParser.parse(searchQuery);
      return indexSearcher.search(query, results);
   }

   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException{
      return indexSearcher.doc(scoreDoc.doc);	
   }
}