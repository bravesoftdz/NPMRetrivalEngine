package ner;

import java.util.ArrayList;
import java.util.List;

import ranking.RankedItem;
import util.PackageManager;
import util.StopWordManager;

public class StringMatching implements EntityExtractor{
	
	public static final String STRING_MATCHING = "str_match";
	
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

	@Override
	public String getTechniqueName() {
		return STRING_MATCHING;
	}

	
}
