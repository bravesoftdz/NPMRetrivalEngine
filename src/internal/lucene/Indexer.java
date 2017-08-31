package internal.lucene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONObject;

import util.ConfigManager;

public class Indexer {

   private IndexWriter writer;


   public Indexer(String indexDirectoryPath) throws IOException{
      //this directory will contain the indexes
      Directory indexDirectory = 
         FSDirectory.open(Paths.get(indexDirectoryPath));

      //create the indexer
      IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
      writer = new IndexWriter(indexDirectory,config);
   }

   public void close() throws CorruptIndexException, IOException{
      writer.close();
   }

   private Document getDocument(JSONObject json) throws IOException{
     
	  Document document = new Document();
      document.add(new StringField(LuceneConstants.ID, (String)json.get(LuceneConstants.ID) , Field.Store.YES));
      document.add(new TextField(LuceneConstants.DESCR, (String)((JSONObject)json.get(LuceneConstants.JSON_VALUE)).get(LuceneConstants.DESCR) , Field.Store.YES));
      document.add(new TextField(LuceneConstants.README, getReadmeString() , Field.Store.YES));

      return document;
   }   

   private String getReadmeString(){
	   return "";
   }
   
   private void indexFile(JSONObject json) throws IOException{
      System.out.println("Indexing "+  (String)json.get("id"));
      Document document = getDocument(json);
      writer.addDocument(document);
   }
   
   
   public int createIndex(String jsondata) {
	    try {
	      FileReader fr = new FileReader(jsondata);
	      BufferedReader br = new BufferedReader(fr);
	      String linea;
	      int count = 0;
	      while((linea = br.readLine()) != null){
	    	  char last = linea.charAt(linea.length()-1);
	    	  if(','==last){
	    		  linea = linea.substring(0, linea.length()-1);
	    	  }
	    	  try{
	    		JSONObject o = new JSONObject(linea);
	    		indexFile(o);
	    	  }catch(Exception e) {
	    	      System.out.println("Excepcion parseando json "+ linea + ": " + e);
	  	      }
	    	  System.out.println(count++);
	      }
	      fr.close();
	    }
	    catch(Exception e) {
	      System.out.println("Excepcion leyendo fichero "+ jsondata + ": " + e);
	    }
	    return writer.numDocs();	
	}

   
   
}