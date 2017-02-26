package modeles;

import indexation.Index;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturerLongueurDoc extends Featurer {
	public FeaturerLongueurDoc(Index idx){
		this.idx = idx;
	}
	@Override
	public ArrayList<Double> getFeatures(String idDoc) {
		ArrayList<Double> f = new ArrayList<Double>();
		HashMap<String,Integer> hm = idx.getTfsForDoc(Integer.parseInt(idDoc));
		double length = 0;
		for(String k : hm.keySet()){
			length += hm.get(k);
		}
		//Note 1 : Afin d’être à même d’apprendre des pondérations efficaces, il conviendra de faire
		//en sorte que tous les Featurers utilisés retournent des scores entre 0 et 1.
		//f.add(length);
		f.add((double)length/100);
		return f;
	}

	@Override
	public ArrayList<Double> getFeatures(String idDoc, String query) {		
		return getFeatures(idDoc);
	}

}
