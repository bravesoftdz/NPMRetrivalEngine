package ner;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import ranking.RankedItem;
import util.PackageManager;
import util.StopWordManager;

public class HiperlinkMatching implements EntityExtractor{
	
	public static final String HYPER_MATCHING = "hyp_match";
	
	@Override
	public List<String> getNamedEntities(Document tech) {
		
		tech.outputSettings(new Document.OutputSettings().prettyPrint(false));

		tech.select("br").append("\\n");
		tech.select("p").prepend("\\n\\n");
		String text = "";
		text = tech.html().replaceAll("\\\\n", "\n");
		
		text = Jsoup.clean(text, "", Whitelist.basic(),
				new Document.OutputSettings().prettyPrint(false));
		
		List<String> entities = new ArrayList<String>();

		PackageManager pkgm = PackageManager.getInstance();

		for (String home : pkgm.getHomePages()) {
			if (text.contains(home)){
				entities.add(pkgm.getPkgName(home));
			}
		}
		
		return entities;
		
	}

	@Override
	public String getTechniqueName() {
		return HYPER_MATCHING;
	}
	
	@Override
	public List<String> getNamedEntities(String text) {
		
		List<String> entities = new ArrayList<String>();

		PackageManager pkgm = PackageManager.getInstance();

		for (String home : pkgm.getHomePages()) {
			if (text.contains(home)){
				entities.add(pkgm.getPkgName(home));
			}
		}
		
		return entities;
		
	}

	
}
