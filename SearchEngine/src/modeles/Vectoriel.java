package modeles;

import indexation.Index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class Vectoriel extends IRmodel{
	private Weighter w;
	private boolean normalized;
	
	public Vectoriel(Index idx) {
		super(idx);		
	}
	public Vectoriel(Weighter w, Index idx, boolean n) {
		super(idx);		
		normalized 	= n;
		this.w		= w;
	}
	
	@Override
	public HashMap<Integer, Double> getScores(HashMap<String, Integer> query) {
		// Trouver tous les ids des documents qui contiennent un des stem de la query:
		int documents = idx.getDocs().size();
		Set<Integer> listeId = idx.getDocs().keySet();
		//System.out.println("la taille du doc : "+documents);
		// Pour chaque document, récupere son text et construire le vecteur avec la query:
			HashMap<Integer,Double> scoresDocs = new HashMap<Integer,Double>();
			for(Integer j : listeId){
				HashMap<String,Double> stems = w.getDocWeightsForDoc(Integer.toString(j));
				SparseVector spDoc 		= new SparseVector(stems.size()+query.size());
				SparseVector spQuery 	= new SparseVector(stems.size()+query.size());
				// Remplir le vecteurDoc et le vecteurQuery par les stems du document
				Set<String> cles = stems.keySet();
				Iterator<String> it = cles.iterator();			
				while (it.hasNext()){
				   String stem 		= (String)it.next(); 
				   Double score		= stems.get(stem);
				   spDoc.put(stem, score);			   
				}						
				// Remplir le vecteur par les stems de la query non présents dans le document
				cles = query.keySet();
				it = cles.iterator();			
				while (it.hasNext()){
				   String stem 	= (String)it.next(); 
				   Double score = query.get(stem).doubleValue();
				   spQuery.put(stem, score);			   	   			  
				}
				if(normalized){
					scoresDocs.put(j, spDoc.cosinSim(spQuery));
				}
				else{
					scoresDocs.put(j, spDoc.dot(spQuery));
				}
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
	
}
