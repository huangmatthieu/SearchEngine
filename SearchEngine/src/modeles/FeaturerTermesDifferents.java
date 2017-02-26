package modeles;

import indexation.Index;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturerTermesDifferents extends Featurer{
	public FeaturerTermesDifferents(Index idx){
		this.idx = idx;
	}
	@Override
	public ArrayList<Double> getFeatures(String idDoc, String query) {
		return getFeatures(idDoc);
	}

	@Override
	public ArrayList<Double> getFeatures(String idDoc) {
		ArrayList<Double> f = new ArrayList<Double>();
		HashMap<String,Integer> hm = idx.getTfsForDoc(Integer.parseInt(idDoc));
		//Note 1 : Afin d’être à même d’apprendre des pondérations efficaces, il conviendra de faire
		//en sorte que tous les F eaturers utilisés retournent des scores entre 0 et 1.
		//f.add((double)hm.size());
		f.add((double)hm.size()/100);
		return f;
	}

}
