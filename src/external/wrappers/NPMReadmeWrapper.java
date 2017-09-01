package external.wrappers;

import java.io.IOException;
import java.net.Proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NPMReadmeWrapper{

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

	public NPMReadmeWrapper() {
	}
	
	public String downloadReadmeContent(String query, Proxy proxy){
		String result = "";
		Document doc = null;
			try {
				if (proxy != null) {
					doc = Jsoup.connect(
							"https://www.npmjs.com/package/" + query)
							.proxy(proxy).userAgent(USER_AGENT).timeout(0).get();
				} else {
					doc = Jsoup.connect(
							"https://www.npmjs.com/package/" + query)
							.userAgent(USER_AGENT).timeout(0).get();
				}
				if (doc.select("div#readme") != null) {
					result = doc.select("div#readme").text();
				}
			} catch (IOException e1) {
				System.out.println("Error estableciendo conexion con NPM.");
			}

		return result;
	}

}