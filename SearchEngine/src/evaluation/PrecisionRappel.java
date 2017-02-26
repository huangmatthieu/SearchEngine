package evaluation;

import indexation.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class PrecisionRappel extends EvalMeasure{
	private int nbLevels;	
	public PrecisionRappel(int nbLevels){
		this.nbLevels = nbLevels;
	}
	@Override
	public ArrayList<Double> eval(IRList l) {
		
		ArrayList<Integer> rankingDocs = new ArrayList<Integer>(l.getDocuments().keySet());	
		ArrayList<String> relevantsDocs = new ArrayList<String>(l.getRequete().getRelevants().keySet());
		ArrayList<Double> precision = new ArrayList<Double>();
		ArrayList<Double> rappel = new ArrayList<Double>();
		int nbDocs = rankingDocs.size();
		int nbRelRet = 0 ;
		int nbRet = 0;
		int nbRel = relevantsDocs.size();
		for(int i = 0 ; i < nbDocs ; i ++){
			nbRet += 1;
			if(l.getRequete().isRelevants(String.valueOf(rankingDocs.get(i)))){
				nbRelRet += 1;
			}
			precision.add(1. * nbRelRet / nbRet);
			rappel.add(1. * nbRelRet / nbRel);
		}
		ArrayList<Double> k = new ArrayList<Double>();
		for (int i = 1; i <= this.nbLevels; i++) {
			k.add( i * 1.0 / this.nbLevels);
		}
		int start = 0;
		ArrayList<Double> interpolation = new ArrayList<Double>();
		for (int i = 0; i < k.size(); i++){
			boolean flag = true;
			double max = 0.0;
			for (int j = start; j < rappel.size(); j++) {
				//Optimisation : evite de reparcourir depuis le debut tout le tableau de rappel pour chaque valeur de k
				if(flag && (i < k.size() - 1) && (rappel.get(j) >= k.get(i+1))){
					start = j;
					flag = false;
				}
				if (rappel.get(j) >= k.get(i)) {
					if (precision.get(j) > max)
						max = precision.get(j);
				}
			}
			interpolation.add(max);
		}
		return interpolation;
	}
  

	
}
