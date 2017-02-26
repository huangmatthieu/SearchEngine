package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indexation.Document;

public class QueryDiv extends Query{

	private HashMap<String,String> relevantsWithSubTop;


	public QueryDiv(Document d,HashMap<String,String> docs) {
		super(d);
		this.relevantsWithSubTop = docs;
	}
	
	public HashMap<String,String> getRelevantsWithSubTop(){ return relevantsWithSubTop; }
	
	public String getIdSubTopic(int idDoc){
		return relevantsWithSubTop.get(idDoc);
	}
	
	public int getNbSubTopics(){
		int cpt =0;
		ArrayList<String> tmp = new ArrayList<String>();
		for(HashMap.Entry<String,String> entry : relevantsWithSubTop.entrySet()){
			if(!tmp.contains(entry.getValue())){
				cpt++;
				tmp.add(entry.getValue());
			}
		}
		return cpt;
	}
	public boolean isRelevants(String idDoc){
		return this.relevantsWithSubTop.containsKey(idDoc);
	}
	
	public HashMap<String,String> getRelevants(){
		return this.relevantsWithSubTop;
		
	}
}
