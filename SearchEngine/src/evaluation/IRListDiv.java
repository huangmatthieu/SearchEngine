package evaluation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class IRListDiv extends IRList{
	private QueryDiv qd; 
	private LinkedHashMap<Integer,Double> rank;

	public IRListDiv(Query requete, Map<Integer, Double> ranking) {
		super(requete, ranking);
		this.qd = (QueryDiv) requete;
	}
	
	
	public String getIdSubTopics(int idDoc){
		return qd.getIdSubTopic(idDoc);
	}
	
	public int getNbTopics(){
		return qd.getNbSubTopics();
	}
	
	public LinkedHashMap<Integer,Double> getRank(){
		return this.rank;
	}

}
