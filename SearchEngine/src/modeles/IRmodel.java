package modeles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import indexation.Index;

public abstract class IRmodel{
	protected Index idx;
	
	public IRmodel(Index idx){
		this.idx = idx;
	}
	
	public abstract HashMap<Integer, Double> getScores(HashMap<String, Integer> query);
	
	public abstract TreeMap<Integer, Double> getRanking(HashMap<String, Integer> query);

	
	public HashMap<Integer,Double> getVectors(int documents, 
			HashMap<String, Integer> query, boolean normalized, Weighter w){
		HashMap<Integer,Double> scoresDocs = new HashMap<Integer,Double>();
		for(int j = 0; j < documents ; j++){
			SparseVector spDoc 		= new SparseVector(documents+query.size());
			SparseVector spQuery 	= new SparseVector(documents+query.size());
			HashMap<String,Double> stems = w.getDocWeightsForDoc(Integer.toString(documents));
			// Remplir le vecteurDoc et le vecteurQuery par les stems du document
			Set cles = stems.keySet();
			Iterator it = cles.iterator();			
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
				scoresDocs.put(documents, spDoc.cosinSim(spQuery));
			}
			else{
				scoresDocs.put(documents, spDoc.dot(spQuery));
			}
		}
		return scoresDocs;
	}

	public void setParams(ArrayList<Double> entry) {
		// TODO Auto-generated method stub
		
	}

}
