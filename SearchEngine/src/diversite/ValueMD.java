package diversite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import evaluation.Query;
import indexation.Index;
import indexation.Stemmer;
import modeles.SparseVector;
import modeles.ValueComparator;

public  class ValueMD implements Value{

	private Index idx;
	private double alpha;
	public ValueMD(Index idx,double alpha){
		this.idx = idx;
		this.alpha=alpha;
	}

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
			sim.put(doc,spQuery.cosinSim(sp));
		}
		ValueComparator comparateur = new ValueComparator(sim);
		TreeMap<Integer,Double> simDocQuery = new TreeMap<Integer,Double>(comparateur);
		simDocQuery.putAll(sim);
		ArrayList<Integer> numDocs = new ArrayList<Integer>(simDocQuery.keySet());
		//Initialisation de la matrice de dissimilarité
		/*HashMap<Integer,HashMap<Integer,Double>> dissimMatrix = new HashMap<Integer,HashMap<Integer,Double>>();
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
				if(!dissimMatrix.containsKey(num)){
					HashMap<Integer,Double> tmp = new HashMap<Integer,Double>();
					tmp.put(num2,1 - res);
					dissimMatrix.put(num, tmp);
				}else{
					dissimMatrix.get(num).put(num2,1 - res);

				}
			}
		}*/
		HashMap<Integer,ArrayList<Double>> sumSim = new HashMap<Integer,ArrayList<Double>>();
		HashMap<Integer,Double> resultat = new HashMap<Integer,Double>();
		HashMap<Integer,Double> argmax = new HashMap<Integer,Double>();
		Integer pivot = numDocs.get(0);
		numDocs.remove(0);
		//argmax.put(pivot,simDocQuery.get(pivot));
		//On va jusqu'a i qui vaut 15
		for(int i = 0 ; i < 15 ; i++){
			//System.out.println("i : "+i);
			for(int j = 0 ; j < numDocs.size(); j++){
				HashMap<String,Integer> tfsnum = this.idx.getTfsForDoc(pivot);
				HashMap<String,Integer> tfsnum2 = this.idx.getTfsForDoc(numDocs.get(j));
				SparseVector sp = new SparseVector(100);
				SparseVector sp2 = new SparseVector(100);
				for(HashMap.Entry<String,Integer> entry : tfsnum.entrySet()){
					sp.put(entry.getKey(), entry.getValue());
				}
				for(HashMap.Entry<String,Integer> entry : tfsnum2.entrySet()){
					sp2.put(entry.getKey(), entry.getValue());
				}
				double res = 1 - sp.cosinSim(sp2);
				//System.out.println(" j : "+j);
				if(!sumSim.containsKey(numDocs.get(j))){
					ArrayList<Double> tmp = new ArrayList<Double>();
					tmp.add(res);
					sumSim.put(numDocs.get(j),tmp);
				}else{
					sumSim.get(numDocs.get(j)).add(res);
				}
				//System.out.println(simMatrix);
				double tmp2 = - (1 / (i + 1)) * sum(sumSim.get(numDocs.get(j)));
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

	private double sum(ArrayList<Double> array){
		double sum = 0;
		for(Double elt : array){
			sum += elt;
		}
		return sum;
	}

}
