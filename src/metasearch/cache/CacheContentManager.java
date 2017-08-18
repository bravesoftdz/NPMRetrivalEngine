package metasearch.cache;

import metasearch.Searcher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CacheContentManager {

	private static CacheContentManager instance = null;

	protected CacheContentManager() {
		//cache = new HashMap<String, Ranking>();
	}

	public static CacheContentManager getInstance() {
		if (instance == null) {
			instance = new CacheContentManager();
		}
		return instance;
	}

	public List loadContentFromCache(Searcher searcher, String query) {
		List contents = new ArrayList();
		String path = "content_cache/" + searcher.getName() +"/"+ query;
		File f = new File(path);
		if (f.exists()){
			File[] ficheros = f.listFiles();
			for (int i=0;i<ficheros.length;i++){
				contents.add(loadFileContent(ficheros[i]));
			}
			return contents;
		} else {
			return null;
		}
	}

	public void saveContentInCache(List results, Searcher searcher, String query) {
		String path = "content_cache/" + searcher.getName() +"/"+ query;
		File directory = new File(path);
		directory.mkdirs();
		for(int i=0;i<results.size();i++){
			String filename = String.valueOf(i)+ ".txt";
			saveFileContent(results.get(i),path,filename);
		}
	}

	public void saveFileContent(Object content, String path, String filename){
		ObjectOutputStream salida = null;
		try {
			File outputFile = new File(path+"/"+filename);
			salida = new ObjectOutputStream(new FileOutputStream(outputFile));
			salida.writeObject(content);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (null != salida)
					salida.close();
			} catch (Exception e2) {
				System.out.println("Excepcion cerrando fichero " +filename+": " + e2);
			}
		}
	}

	protected Object loadFileContent(File file) {
		Object content = null;
		ObjectInputStream entrada = null;
		try {
			entrada = new ObjectInputStream(new FileInputStream(file));
			content = entrada.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != entrada)
					entrada.close();
			} catch (Exception e2) {
				System.out.println("Excepcion cerrando fichero " +file+": " + e2);
			}
		}
		return content;
	}

}
