package modeles;

import indexation.Index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class W2 extends Weighter{
	private Index i;
	// w(t,d) = tf(t,d) et W(t,q) = tf(t,q)
	public W2(Index i){
		this.i = i;
	}

	@Override
	HashMap<String, Double> getDocWeightsForDoc(String idDoc) {
		int id = Integer.parseInt(idDoc);
		HashMap<String, Double> ret= new HashMap<String, Double>();
		HashMap<String,Integer> hm = i.getTfsForDoc(id);
		Set cles = hm.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
		   String stem 	= (String)it.next(); 
		   Integer tf 	= (Integer)hm.get(stem);
		   Double weight= tf.doubleValue();
		   ret.put(stem, weight);
		}
		return ret;
	}

	@Override
	HashMap<Integer, Double> getDocWeightsForStem(String stem) {
		HashMap<Integer, Double> ret= new HashMap<Integer, Double>();
		HashMap<Integer,Integer> hm = i.getTfsForStem(stem);
		Set cles = hm.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
		   Integer id 	= (Integer)it.next(); 
		   Integer tf 	= (Integer)hm.get(id);
		   Double weight= tf.doubleValue();
		   ret.put(id, weight);
		}
		return ret;
	}

	@Override
	HashMap<String, Double> getWeightsForQuery(HashMap<String, Integer> query) {
		HashMap<String, Double> ret= new HashMap<String, Double>();
		Set cles = query.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
		   String stem 	= (String)it.next(); 
		   Integer tf 	= (Integer)query.get(stem);
		   Double weight= tf.doubleValue();
		   ret.put(stem, weight);
		}
		return ret;
	}

}
