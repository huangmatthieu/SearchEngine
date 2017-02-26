package modeles;

import indexation.MyRandomAccessFile;
import indexation.Stemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class FeaturerModel extends Featurer{
	private IRmodel modele;	
	public FeaturerModel(IRmodel modele){
		this.modele = modele;
		this.features = new HashMap<String, HashMap<Integer, Double>>();
	}
	@Override
	public ArrayList<Double> getFeatures(String idDoc, String query) {
		ArrayList<Double> f = new ArrayList<Double>();
		Stemmer s = new Stemmer();
		double score;
		if(!features.containsKey(query)){
			features.put(query, modele.getScores(s.getTextRepresentation(query)));
		}
		score = get2(idDoc,query);			
		//f.add(score/100);
		f.add(1.0/(1-Math.exp(-score)));
		return f;
	}
	private Double get2(String idDoc, String query){
		int id 			= Integer.parseInt(idDoc);					
		return features.get(query).get(id);
	}
	@Override
	public ArrayList<Double> getFeatures(String idDoc) {
		// VIDE
		return null;
	}	
}
