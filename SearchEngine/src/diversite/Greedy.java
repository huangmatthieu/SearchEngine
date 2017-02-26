package diversite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import evaluation.Query;

public class Greedy{
	private ArrayList<Integer> documents;
	private int k;
	private Value val;

	public Greedy(ArrayList<Integer> documents, int k,Value val){
		this.documents = documents;
		this.k= k;
		this.val = val;
	}

	public LinkedHashMap<Integer,Double> getRanking(HashMap<String, Integer> query) throws FileNotFoundException, ClassNotFoundException, IOException{
		LinkedHashMap<Integer,Double> ranking = new LinkedHashMap<Integer,Double>();
		ArrayList<Integer> copyDoc = new ArrayList<Integer>();
		copyDoc.addAll(this.documents);
		for(int i = 0 ; i < this.k ; i++){
			Integer tmp = val.value(query,copyDoc);
			System.out.println("doc : "+tmp);
			int index  = copyDoc.indexOf(tmp);
			ranking.put(tmp,1.);
			copyDoc.remove(index);
		}
		System.out.println("done");
		return ranking;
	}
}
