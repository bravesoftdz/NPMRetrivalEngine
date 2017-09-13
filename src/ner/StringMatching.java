package ner;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import ranking.RankedItem;
import util.PackageManager;
import util.StopWordManager;

public class StringMatching implements EntityExtractor{
	
	public static final String STRING_MATCHING = "str_match";
	
	@Override
	public List<String> getNamedEntities(Document tech) {
		
		tech.outputSettings(new Document.OutputSettings().prettyPrint(false));

		tech.select("br").append("\\n");
		tech.select("p").prepend("\\n\\n");
		String text = "";
		text = tech.html().replaceAll("\\\\n", "\n");
		text = Jsoup.clean(text, "", Whitelist.none(),
				new Document.OutputSettings().prettyPrint(false));
		
		List<String> entities = new ArrayList<String>();
		
		String[] words = text.split(" ");
		PackageManager pkgm = PackageManager.getInstance();
		StopWordManager swm = StopWordManager.getInstance();

		for (String word : words) {
			word = word.trim().toLowerCase();
			if (!"".equals(word) && !swm.isStopWord(word) && pkgm.isPkgName(word)) {
				entities.add(word);
			}
		}
		
		return entities;
		
	}

	@Override
	public String getTechniqueName() {
		return STRING_MATCHING;
	}

	@Override
	public List<String> getNamedEntities(String text) {
		
		List<String> entities = new ArrayList<String>();
		
		String[] words = text.split(" ");
		PackageManager pkgm = PackageManager.getInstance();
		StopWordManager swm = StopWordManager.getInstance();

		for (String word : words) {
			word = word.trim().toLowerCase();
			if (!"".equals(word) && !swm.isStopWord(word) && pkgm.isPkgName(word)) {
				entities.add(word);
			}
		}
		
		return entities;
		
	}
	
}
