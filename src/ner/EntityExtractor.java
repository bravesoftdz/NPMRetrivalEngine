package ner;

import java.util.List;

public interface EntityExtractor {
	
	public List<String> getNamedEntities(String text);
	
	public String getTechniqueName();

}
