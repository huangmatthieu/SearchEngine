package modeles;

import indexation.Index;

import java.util.HashMap;
import java.util.Set;

public class PageRanks extends RandomWalk{
	private double d;
	private HashMap<String,Double> mu;
	private int it;

	public PageRanks(int it){
		this.it = it;
	}

	public HashMap<String,Double> marcheAleatoire(HashMap<String,String> suc,HashMap<String,String> pred) {
		//this.idx=idx;	
		int N 			= pred.size();
		this.mu = new HashMap<String,Double>();
		HashMap<String,Double> muOp = new HashMap<String,Double>();
		d		 		= Math.random();
		//HashMap<String,String> suc = idx.getSuccesseurs();
		//HashMap<String,String> pred = idx.getPredecesseurs();
		/*int ls=0;
		for(String i:suc.keySet()){
			String si=suc.get(i);
			String s[]=si.split(";");
			ls+=s.length;
			for(String j:s){
				String pj=pred.get(j);
				String t[]=pj.split(";");
				boolean ok=false;
				for(int k=0;k<t.length;k++){
					if(t[k].equals(i)){
						ok=true;
						break;
					}
				}
				if(!ok){
					throw new RuntimeException(i+" pas predecesseur de son succ "+j);
				}
			}
		}
		int lp=0;
		for(String i:pred.keySet()){
			String si=pred.get(i);
			String s[]=si.split(";");
			lp+=s.length;
			for(String j:s){
				String pj=suc.get(j);
				String t[]=pj.split(";");
				boolean ok=false;
				for(int k=0;k<t.length;k++){
					if(t[k].equals(i)){
						ok=true;
						break;
					}
				}
				if(!ok){
					throw new RuntimeException(i+" pas suc de son pred "+j);
				}
			}
		}
		System.out.println("preds = "+lp + " succs = "+ls); */
		/*suc=new HashMap<String,String>();
		pred=new HashMap<String,String>();
		suc.put("1", "2;3");
		suc.put("2", "1;5");
		suc.put("3", "2;4");
		suc.put("4","1;2");
		suc.put("5","1");
		pred.put("1", "2;4;5");
		pred.put("2", "1;3;4");
		pred.put("3", "1");
		pred.put("4","3");
		pred.put("5","2");
		N = 5; 
		System.out.println("taille suc : "+suc.size() );
		System.out.println("taille pred : "+pred.size() );*/
		//initialisation du mu initial a 1/N et de muOp a (1-d)/N car dans la matrice on ne traite que les pages ayant des prédecesseurs.
		for(HashMap.Entry<String,String> entry : pred.entrySet()){
			mu.put(entry.getKey(),1./N); 
			muOp.put(entry.getKey(),(1 - this.d) / N);
		}
		for(HashMap.Entry<String,String> entry : suc.entrySet()){
			mu.put(entry.getKey(),1./N); 
			muOp.put(entry.getKey(),(1 - this.d) / N);
		}
		int iter = 0;
		double conv;
		do{
			//recuperation de toutes les pages ayant des prédecesseurs
			Set<String> cle =  pred.keySet();
			String [] Pi = cle.toArray(new String[pred.size()]);

			//Pour chaque Pi ayant au moins un prédécesseur
			for(int i = 0 ; i < Pi.length ; i++){				
				//les predecesseurs de Pi => j
				String[] tabPred = pred.get(Pi[i]).split(";");
				double acc = 0;
				// j appartenant à Pi
				for(int j = 0 ; j < tabPred.length ; j++){
					//tableau contenant touts les successeurs de j, cad tous les liens sortant de la page j
					String[] jsucclength = suc.get(tabPred[j]).split(";");
					acc += this.mu.get(tabPred[j]) / jsucclength.length; 
				}			
				acc = ((1 - this.d) / (N)) + (acc * this.d);
				muOp.put(Pi[i],acc);
			}			
			//verification de la convergence
			conv = 0;
			for(HashMap.Entry<String,String> entry : pred.entrySet()){
				conv += Math.pow(mu.get(entry.getKey()) - muOp.get(entry.getKey()),2);
			}
			System.out.println("Valeur de conv iération numero "+iter+" : "+conv);
			double sum = 0;
			for(HashMap.Entry<String,String> entry : pred.entrySet())
				sum += mu.get(entry.getKey());
			System.out.println("la somme du vecteur mu :"+sum);
			//reinitialisation des mu
			for(HashMap.Entry<String,Double> entry : muOp.entrySet()){
				this.mu.put(entry.getKey(), entry.getValue());
			}
			for(HashMap.Entry<String,Double> entry : muOp.entrySet()){
				muOp.put(entry.getKey(),(1 - this.d)/(N));
			}
			iter++;
			System.out.println("------------------------------");
		}while(iter < this.it);
		return this.mu;
	}	
}
