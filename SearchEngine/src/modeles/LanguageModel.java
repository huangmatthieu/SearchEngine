package modeles;

import indexation.Document;
import indexation.Index;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import evaluation.Query;

public class LanguageModel extends IRmodel{
	private double lambda;
	public LanguageModel(Index idx) {
		super(idx);
		lambda = Math.random();
	}

	@Override
	public HashMap<Integer, Double> getScores(HashMap<String, Integer> query) {
		int documents = idx.getDocs().size();		
		Set<Integer> listeId = idx.getDocs().keySet();
		HashMap<Integer,Double> scoresDocs = new HashMap<Integer,Double>();
		for(Integer j : listeId){					
			HashMap<String,Integer> document = idx.getTfsForDoc(j);			
			double score = f(document, query);			
			scoresDocs.put(j, score);
		}
		return scoresDocs;
	}
	public TreeMap<Integer, Double> getRanking(HashMap<String, Integer> query){
		HashMap<Integer,Double> scores  = this.getScores(query);
		ValueComparator comparateur = new ValueComparator(scores);
		TreeMap<Integer,Double> mapTriee = new TreeMap<Integer,Double>(comparateur);
		mapTriee.putAll(scores);
		return mapTriee;		
	}
	private double f(HashMap<String,Integer> document, HashMap<String, Integer> query){
		double s=0;
		for (Entry<String, Integer> entree : query.entrySet()) {
			String terme = entree.getKey();
			//System.out.println(terme);
			s += tf(terme,query)*Math.log(lambda*pm(document,terme) + (1-lambda)*pmc(terme));
			//System.out.println("lambda*pm(document,terme) = "+lambda*pm(document,terme));
			//System.out.println("(1-lambda)*pmc(terme) = "+(1-lambda)*pmc(terme));
			//System.out.println(s);
		}
		return s;
	}	
	private double tf(String terme, HashMap<String,Integer> document){		
		int t 		= document.get(terme);
		double s	= 0;
		for (Entry<String, Integer> entree : document.entrySet()) {
			Integer r  = entree.getValue();
			s += r;
		}		
		return (double)t/s;
	}
	private double pm(HashMap<String,Integer> document, String terme){
		if(document.containsKey(terme)){
			double a = document.get(terme);		
			return a/L(document);
		}
		return 0;
	}
	private double pmc(String terme){		
		HashMap<Integer,Integer> hm = idx.getTfsForStem(terme);
		double a = hm.size();
		double b = idx.getStemsLength();
		return a/b;
	}
	// Le nombre de termes (non vides) du doc d. Soit la somme des poids de ses termes.
	private double L(HashMap<String, Integer> document){ 
		double s=0;
		for (Entry<String, Integer> entree : document.entrySet()) {			
			Integer tf = entree.getValue();
			s+=tf;
		}
		return s;
	}
}
