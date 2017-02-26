package evaluation;

import indexation.Document;
import indexation.Stemmer;

import java.util.HashMap;

public class Query {
	protected String id;
	protected String text;
	private HashMap<String,String> relevants = new HashMap<String,String>();
	
	public Query(Document d){
		this.id = d.getId();
		this.text = d.getText();
	}
	public Query(Document d, String docs){
		this.id 	= d.getId();
		this.text 	= d.getText();
		String[] s 	= docs.split(",");
		for(String str : s){
			relevants.put(str, "0.0");
		}
	}
	public String getId(){ return id;}
	public String getText(){ return text.trim();}
	public HashMap<String,String> getRelevants(){ return relevants; }
	public boolean isRelevants(String idDoc){
		return relevants.containsKey(idDoc);
	}

}
