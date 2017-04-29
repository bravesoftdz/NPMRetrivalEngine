package metasearch.cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import metasearch.Searcher;
import ranking.Ranking;

public class CacheManager {

	private static CacheManager instance = null;
	private HashMap<String, Ranking> cache = null;

	protected CacheManager() {
		cache = new HashMap<String, Ranking>();
		loadCache();
	}

	public static CacheManager getInstance() {
		if (instance == null) {
			instance = new CacheManager();
		}
		return instance;
	}

	protected void loadCache() {
		ObjectInputStream entrada = null;
		try {
			entrada = new ObjectInputStream(new FileInputStream("cache2.dat"));
			cache = (HashMap<String, Ranking>) entrada.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != entrada)
					entrada.close();
			} catch (Exception e2) {
				System.out.println("Excepcion cerrando fichero cache2.dat: " + e2);
			}
		}
	}

	public void saveCache() {
		ObjectOutputStream salida = null;
		try {
			salida = new ObjectOutputStream(new FileOutputStream("cache2.dat"));
			salida.writeObject(cache);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != salida)
					salida.close();
			} catch (Exception e2) {
				System.out.println("Excepcion cerrando fichero cache2.dat: " + e2);
			}
		}
	}

	public boolean isCached(Searcher searcher, String query) {
		return cache.containsKey(getKey(searcher, query));
	}

	public Ranking getCached(Searcher searcher, String query) {
		return cache.get(getKey(searcher, query));
	}

	public void save(Searcher searcher, String query, Ranking ranking) {
		cache.put(getKey(searcher, query), (Ranking) ranking);
	}

	private String getKey(Searcher searcher, String query) {
		return searcher.getName() + "::::" + query;
	}
}
