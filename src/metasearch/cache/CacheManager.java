package metasearch.cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metasearch.Searcher;

public class CacheManager {

	private static CacheManager instance = null;
	private HashMap<String, List<String>> cache = null;

	protected CacheManager() {
		cache = new HashMap<String, List<String>>();
		loadCache();
	}

	public static CacheManager getInstance() {
		if (instance == null) {
			instance = new CacheManager();
		}
		return instance;
	}

	protected void loadCache() {
		ObjectInputStream entrada;
		try {
			entrada = new ObjectInputStream(new FileInputStream("cache.dat"));
			cache = (HashMap<String, List<String>>) entrada.readObject();
			entrada.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveCache() {
		ObjectOutputStream salida;
		try {
			salida = new ObjectOutputStream(new FileOutputStream("cache.dat"));
			salida.writeObject(cache);
			salida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isCached(Searcher searcher, String query) {
		return cache.containsKey(getKey(searcher,query));
	}

	public List<String> getCached(Searcher searcher, String query) {
		return cache.get(getKey(searcher,query));
	}

	public void save(Searcher searcher, String query, List<String> ranking) {
		cache.put(getKey(searcher,query), ranking);
	}
	
	private String getKey(Searcher searcher, String query){
		return searcher.getName()+"::::"+query;
	}
}
