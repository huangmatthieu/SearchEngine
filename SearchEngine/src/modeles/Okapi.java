package modeles;

import indexation.Document;
import indexation.Index;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Okapi extends IRmodel{
	private double Lmoy;
	public Okapi(Index idx) {
		super(idx);	
		HashMap<Integer, String> docs  = idx.getDocs();
		Double somme = 0.0;
		for(HashMap.Entry<Integer,String> entry : docs.entrySet()){
			HashMap<String,Integer> tfs = idx.getTfsForDoc(entry.getKey());
			for(HashMap.Entry<String,Integer> ent : tfs.entrySet()){
				somme += ent.getValue();
			}
		} 
		Lmoy	= somme / idx.getNbDocs(); // Longueur moyenne
	}

	@Override
	public HashMap<Integer, Double> getScores(HashMap<String, Integer> query) {
		Set<Integer> listeId = idx.getDocs().keySet();
		HashMap<Integer,Double> scoresDocs = new HashMap<Integer,Double>();
		for(Integer j : listeId){	// Parcours des documents.
			//System.out.println("document "+j+" : ");
			HashMap<String,Integer> document = idx.getTfsForDoc(j);
			double score = f(document, query);
			//System.out.println("score : "+score);
			scoresDocs.put(j, score);
		}
		return scoresDocs;
	}
	public TreeMap<Integer, Double> getRanking(HashMap<String, Integer> query){
		HashMap<Integer,Double> scores  = this.getScores(query);
		ValueComparator comparateur = new ValueComparator(scores);
		TreeMap<Integer,Double> mapTriee = new TreeMap<Integer,Double>(comparateur);
		mapTriee.putAll(scores);
		return mapTriee;		
	}
	public double f(HashMap<String, Integer> document, HashMap<String, Integer> query){
		double s	= 0; // somme retournée
		double k1	= 1.5; // k1 paramètre entre 1 et 2 						
		double b 	= 0.75; // b paramètre proche de 0.75
		//System.out.println(L(document)+" "+Lmoy);
		for (Entry<String, Integer> entree : query.entrySet()) {
			String terme = entree.getKey();
			s+=idf(terme)*(k1+1)*tf(terme,document)/(k1*((1-b)+ b*L(document)/Lmoy)+tf(terme,document));
		}
		return s;
	}
	
	
	private double idf(String terme){
		double a = df(terme) + 0.5;
		double N = idx.getDocs().size();	
		return max(0, Math.log((N-a)/a));
	}	
	private double df(String terme){
		HashMap<Integer,Integer> hm = idx.getTfsForStem(terme);
		double a = hm.size();
		double b = idx.getNbDocs();
		return a/b;
	}
	private double tf(String terme, HashMap<String,Integer> document){
		if(document.containsKey(terme)){
			int t = document.get(terme);		
			double s=0;
			for (Entry<String, Integer> entree : document.entrySet()) {
				Integer r  = entree.getValue();
				s+=r;
			}
			return (double)t/s;
		}
		return 0;
	}
	// Le nombre de termes (non vides) du doc d. Soit la somme des poids de ses termes.
	private double L(HashMap<String, Integer> document){ 
		Integer somme = 0;
		for(HashMap.Entry<String,Integer> entry : document.entrySet()){
			somme += entry.getValue();
		}
		return (double) somme;
	}
	
	private double max(double a, double b){
		if(a>b) return a;
		return b;
	}
}
