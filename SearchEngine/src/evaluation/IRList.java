package evaluation;

import java.util.Map;
import java.util.TreeMap;


public class IRList {
	private Query requete;
	private Map<Integer, Double> ranking;
	public IRList(Query requete, Map<Integer,Double> ranking){
		this.setRequete(requete);
		this.setDocuments(ranking);
	}	
	
	public Query getRequete() {
		return requete;
	}
	public void setRequete(Query requete) {
		this.requete = requete;
	}
	public Map<Integer, Double> getDocuments() {
		return ranking;
	}
	public void setDocuments(Map<Integer, Double> documents) {
		this.ranking = documents;
	}

}
