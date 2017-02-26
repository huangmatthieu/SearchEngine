package modeles;

import java.util.HashMap;

public abstract class Weighter {
	/**
	 * Retourne les poids des termes pour un document dont l'identifant est passé en paramètre;
	 * @param idDoc
	 * @return HashMap<String,Double>
	 */
	abstract HashMap<String,Double> getDocWeightsForDoc(String idDoc);
		
	/**
	 * retourne les poids du terme stem dans tous les documents qui le contiennent; 
	 * @param stem HashMap <String,Double> 
	 * @return
	 */
	abstract HashMap<Integer,Double> getDocWeightsForStem(String stem);	
	
	/**
	 * retourne les poids des termes pour la requête dont les tf sont passés en paramètres.
	 * @param query HashMap <String,Integer> 
	 * @return
	 */
	abstract HashMap <String,Double> getWeightsForQuery(HashMap <String,Integer> query);
}
