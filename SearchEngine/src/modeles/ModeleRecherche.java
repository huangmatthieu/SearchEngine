package modeles;

import indexation.Index;
import indexation.Stemmer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import modeles.IRmodel;
import evaluation.Query;

public class ModeleRecherche extends IRmodel{
	private IRmodel model;
	private int n,k;
	ArrayList<Integer> Vq;
	HashMap<String,String> successeurs;
	HashMap<String,String> predecesseurs;
	
	public ModeleRecherche(Index i, IRmodel m, int n, int k){
		super(i);
		this.model 	= m;
		this.n 		= n;
		this.k 		= k;
		this.Vq     = new ArrayList<Integer>();
		successeurs	= new HashMap<String,String>();
		predecesseurs	= new HashMap<String,String>();
	}
	
	private void SousGraphe(HashMap<String, Integer> q){		
		// Initialisation de Vq avec un ensemble S de documents Seed
		Vq.addAll(getNFirst(q));
		// Pour chaque document D dans S, ajout dans Vq de tous les documents pointÃ©s par D...
		ArrayList<Integer> s = getSucc();		
		// ... Et de k documents qui pointent vers D choisis aleatoirement.
		ArrayList<Integer> p = getPrec();
		Vq.addAll(s);		
		Vq.addAll(p);
	}
	
	private ArrayList<Integer> getNFirst(HashMap<String, Integer> query){
		ArrayList<Integer> nFirst = new ArrayList<Integer>(); 	
		TreeMap<Integer, Double> ranking = model.getRanking(query);		
		int i=1;
		for(Map.Entry<Integer,Double> entry : ranking.entrySet()) {
			Integer key = entry.getKey();		  
			nFirst.add(key);
			//System.out.println(nFirst.size());
			if(i++>=n) break;
		}
		return nFirst;
	}
	
	private ArrayList<Integer> getSucc(){
		ArrayList<Integer> allSuccesseurs = new ArrayList<Integer>();
		for(Integer i : Vq){			
			String suc = idx.getSuccesseurs().get(String.valueOf(i));
			if(suc == null)
				continue;
			String[] sucTab = suc.split(";");
			successeurs.put(String.valueOf(i), suc);
			updatePrec(sucTab, String.valueOf(i));			
			for(int j = 0 ; j < sucTab.length ; j++){
				if(allSuccesseurs.contains(Integer.parseInt(sucTab[j]))){
					continue;
				}					
				allSuccesseurs.add(Integer.parseInt(sucTab[j]));
			}

		}
		return allSuccesseurs;
	}
	
	private ArrayList<Integer> getPrec(){
		ArrayList<Integer> kPredecesseurs = new ArrayList<Integer>();
		ArrayList<String> shufflePred = new ArrayList<String>();
		for(Integer i : Vq){
			String prec = idx.getPredecesseurs().get(String.valueOf(i));
			if(prec == null)
				continue;
			String[] tabPred = prec.split(";");
			for(int j = 0; j < tabPred.length ; j++){
				shufflePred.add(tabPred[j]);
			}
			//Randomise les predecesseurs
			Collections.shuffle(shufflePred);
			prec = "";
			int kk = 0;
			while(kk < this.k && kk < shufflePred.size()){
				kPredecesseurs.add(Integer.parseInt(shufflePred.get(kk)));
				if(kk == this.k-1){
					prec += shufflePred.get(kk);
				}else{
					prec += shufflePred.get(kk)+";";
				}
				
				String debut = shufflePred.get(kk)+";";
			    String milieu = ";"+shufflePred.get(kk)+";";
			    String fin = ";"+shufflePred.get(kk);

				if(!predecesseurs.containsKey(String.valueOf(i)))
					predecesseurs.put(String.valueOf(i), shufflePred.get(kk));
				else if(!predecesseurs.get(String.valueOf(i)).contains(debut) || !predecesseurs.get(String.valueOf(i)).contains(milieu) || !predecesseurs.get(String.valueOf(i)).contains(fin)){
					predecesseurs.put(String.valueOf(i), predecesseurs.get(String.valueOf(i))+';'+shufflePred.get(kk));
				}
				
				kk++;
			}
			
			String[] tabPred2 = prec.split(";");
			updateSuc(tabPred2,String.valueOf(i));
		}		
		return kPredecesseurs;
	}
	public HashMap<String,String> getSuccesseurs(){		
		return successeurs;
	}
	public HashMap<String,String> getPredecesseurs(){		
		return predecesseurs;
	}
	public int getNbDocs(){		
		return Vq.size();
	}
	public ArrayList<Integer> getVq() { return Vq; }

	private void updatePrec(String[] sucTab, String id){
		for(int i=0; i<sucTab.length; i++){
			String debut = sucTab[i]+";";
			String milieu = ";"+sucTab[i]+";";
			String fin = ";"+sucTab[i];
			if(!predecesseurs.containsKey(sucTab[i]))
				predecesseurs.put(sucTab[i], id);
			else if(!predecesseurs.get(sucTab[i]).contains(debut) || !predecesseurs.get(sucTab[i]).contains(milieu) || !predecesseurs.get(sucTab[i]).contains(fin)){
				predecesseurs.put(sucTab[i], predecesseurs.get(sucTab[i])+';'+id);
			}
		}
	}	
	private void updateSuc(String[] predTab, String id){
		for(int i=0; i<predTab.length; i++){
			String debut = predTab[i]+";";
			String milieu = ";"+predTab[i]+";";
			String fin = ";"+predTab[i];
			if(!successeurs.containsKey(predTab[i]))
				successeurs.put(predTab[i], id);
			else if (!successeurs.get(predTab[i]).contains(debut) || !successeurs.get(predTab[i]).contains(milieu) || !successeurs.get(predTab[i]).contains(fin)){
				successeurs.put(predTab[i], successeurs.get(predTab[i])+';'+id);
			}
		}
	}

	public HashMap<Integer, Double> getScores(HashMap<String, Integer> query) {
		// TODO Auto-generated method stub
		SousGraphe(query);
		PageRanks pr = new PageRanks(20);
		HashMap<String,Double> mu = pr.marcheAleatoire(getSuccesseurs(),getPredecesseurs());
		HashMap<Integer,Double> muConv = new HashMap<Integer, Double>();
		for(HashMap.Entry<String,Double> entry : mu.entrySet()){
			muConv.put(Integer.parseInt(entry.getKey()),entry.getValue());
		}
		return muConv;
	}
	
	public TreeMap<Integer, Double> getRanking(HashMap<String, Integer> query){
		HashMap<Integer,Double> mu = getScores(query);
		ValueComparator comparateur = new ValueComparator(mu);
		TreeMap<Integer,Double> mapTriee = new TreeMap<Integer,Double>(comparateur);
		mapTriee.putAll(mu);
		//System.out.println(mapTriee);
		return mapTriee;
	}
	

}
