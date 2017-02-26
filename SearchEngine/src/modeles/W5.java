package modeles;

import indexation.Index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class W5 extends Weighter {
	private Index i;
	private int nbDoc;

	public W5(Index i){
		this.i = i ;
		this.nbDoc = i.getNbDocs();
	}

	@Override
	HashMap<String, Double> getDocWeightsForDoc(String idDoc) {
		int id = Integer.parseInt(idDoc);
		HashMap<String, Double> ret= new HashMap<String, Double>();
		HashMap<String,Integer> hm = i.getTfsForDoc(id);
		System.out.println(id);
		System.out.println(hm);
		Set<String> cles = hm.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()){
			String stem 	= (String)it.next(); 
			Integer tf 	= (Integer)hm.get(stem);
			HashMap<Integer,Integer> tfsStem = i.getTfsForStem(stem);
			int tfsStem2 =0 ;
			System.out.println(" yooé "+stem);
			for(HashMap.Entry<Integer,Integer> entry : tfsStem.entrySet()){
				tfsStem2 += entry.getValue();
			}
			System.out.println(tfsStem2);
			double idf = Math.log(this.nbDoc / tfsStem2);
			
			Double weight= 1 + Math.log(tf.doubleValue()) * idf;
			ret.put(stem, weight);
		}
		return ret;
	}

	@Override
	HashMap<Integer, Double> getDocWeightsForStem(String stem) {
		HashMap<Integer, Double> ret= new HashMap<Integer, Double>();
		HashMap<Integer,Integer> hm = i.getTfsForStem(stem);
		Set<Integer> cles = hm.keySet();
		Iterator<Integer> it = cles.iterator();
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
		Set<String> cles = query.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()){
			String stem 	= (String)it.next(); 
			Integer tf 	= (Integer)query.get(stem);
			HashMap<Integer,Integer> tfsStem = i.getTfsForStem(stem);
			int tfsStem2 =0 ;
			for(HashMap.Entry<Integer,Integer> entry : tfsStem.entrySet()){
				tfsStem2 += entry.getValue();
			}
			double idf = Math.log(this.nbDoc / tfsStem2);
			ret.put(stem,1 + Math.log(tf) * idf);
		}
		return ret;
	}

}
