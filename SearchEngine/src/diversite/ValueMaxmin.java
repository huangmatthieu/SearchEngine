package diversite;

import indexation.Index;
import indexation.Stemmer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import modeles.SparseVector;
import modeles.ValueComparator;
import evaluation.Query;

public class ValueMaxmin implements Value {
	
	private Index idx;
	private double alpha;
	public ValueMaxmin(Index idx){
		this.idx = idx;
		this.alpha=0;
	}

	@Override
	public Integer value(HashMap<String, Integer> query,ArrayList<Integer> documents) {
		Stemmer stemmer = new Stemmer();
		//similarité cosinus des docs avec la requete
		HashMap<Integer,Double> sim = new HashMap<Integer,Double>();
		SparseVector spQuery = new SparseVector(100);
		for(HashMap.Entry<String,Integer> entry : query.entrySet()){
			spQuery.put(entry.getKey(), entry.getValue());
		}
		for(Integer doc : documents){
			HashMap<String,Integer> res = this.idx.getTfsForDoc(doc);
			SparseVector sp = new SparseVector(100);
			for(HashMap.Entry<String,Integer> entry : res.entrySet()){
				sp.put(entry.getKey(), entry.getValue());
			}
			sim.put(doc, spQuery.cosinSim(sp));
		}
		ValueComparator comparateur = new ValueComparator(sim);
		TreeMap<Integer,Double> simDocQuery = new TreeMap<Integer,Double>(comparateur);
		simDocQuery.putAll(sim);
		ArrayList<Integer> numDocs = new ArrayList<Integer>(simDocQuery.keySet());
		//System.out.println(numDocs);
		//Initialisation de la matrice de similarité
		HashMap<Integer,HashMap<Integer,Double>> simMatrix = new HashMap<Integer,HashMap<Integer,Double>>();
		for(Integer num : numDocs){
			for(Integer num2 : numDocs){
				HashMap<String,Integer> tfsnum = this.idx.getTfsForDoc(num);
				HashMap<String,Integer> tfsnum2 = this.idx.getTfsForDoc(num2);
				SparseVector sp = new SparseVector(100);
				SparseVector sp2 = new SparseVector(100);
				for(HashMap.Entry<String,Integer> entry : tfsnum.entrySet()){
					sp.put(entry.getKey(), entry.getValue());
				}
				for(HashMap.Entry<String,Integer> entry : tfsnum2.entrySet()){
					sp2.put(entry.getKey(), entry.getValue());
				}
				double res = sp.cosinSim(sp2);
				if(!simMatrix.containsKey(num)){
					HashMap<Integer,Double> tmp = new HashMap<Integer,Double>();
					tmp.put(num2,res);
					simMatrix.put(num, tmp);
				}else{
					simMatrix.get(num).put(num2, res);
					
				}
			}
		}
		HashMap<Integer,ArrayList<Double>> maxSim = new HashMap<Integer,ArrayList<Double>>();
		HashMap<Integer,Double> resultat = new HashMap<Integer,Double>();
		HashMap<Integer,Double> argmax = new HashMap<Integer,Double>();
		Integer pivot = numDocs.get(0);
		numDocs.remove(0);
		//argmax.put(pivot,simDocQuery.get(pivot));
		//On va jusqu'a i qui vaut 15
		for(int i = 0 ; i < 15 ; i++){
			//System.out.println("i : "+i);
			for(int j = 0 ; j < numDocs.size(); j++){
				//System.out.println(" j : "+j);
				if(!maxSim.containsKey(numDocs.get(j))){
					ArrayList<Double> tmp = new ArrayList<Double>();
					tmp.add(simMatrix.get(pivot).get(numDocs.get(j)));
					maxSim.put(numDocs.get(j),tmp);
				}else{
					maxSim.get(numDocs.get(j)).add(simMatrix.get(pivot).get(numDocs.get(j)));
				}
				//System.out.println(simMatrix);
				double tmp2 = Collections.max(maxSim.get(numDocs.get(j)));
				resultat.put(numDocs.get(j), this.alpha * simDocQuery.get(numDocs.get(j)) - (1- this.alpha) * tmp2);
			}
			ValueComparator comparateur2 = new ValueComparator(resultat);
			TreeMap<Integer,Double> resultat2 = new TreeMap<Integer,Double>(comparateur2);
			resultat2.putAll(resultat);
			ArrayList<Integer> docTrie = new ArrayList<Integer>(resultat2.keySet());
			pivot = docTrie.get(0);
			argmax.put(pivot, resultat2.get(pivot));
			int index = numDocs.indexOf(pivot);
			numDocs.remove(index);
			resultat.remove(pivot);
		}
		ValueComparator comparateur3 = new ValueComparator(argmax);
		TreeMap<Integer,Double> resultat3 = new TreeMap<Integer,Double>(comparateur3);
		resultat3.putAll(argmax);
		//System.out.println(resultat3);
		ArrayList<Integer> docValueMax = new ArrayList<Integer>(resultat3.keySet());
		return docValueMax.get(0);
	}
}
