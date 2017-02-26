package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import indexation.Stemmer;
import modeles.IRmodel;

public class EvalIRModel {
	private IRmodel model;
	private EvalMeasure mesure;
	private ArrayList<Query> ensRequetes;
	private ArrayList<Double> moyenne;
	private ArrayList<Double> variance;
	private ArrayList<Double> std;
	public EvalIRModel(IRmodel model, EvalMeasure mesure, ArrayList<Query> ensRequetes){
		//moyenne et ecart-type pour chaque modele
		this.model 			= model;
		this.mesure 		= mesure;
		this.ensRequetes 	= ensRequetes;	
	}
	
	public void evaluer(){
		Stemmer stemmer = new Stemmer();
		HashMap<String,ArrayList<Double>> hm = new HashMap<String,ArrayList<Double>>(); 
		for(Query requete : ensRequetes){
			Map<Integer,Double> ranking  = this.model.getRanking(stemmer.getTextRepresentation(requete.getText()));
			IRList liste = new IRList(requete, ranking);
			//System.out.println("les doc pertinents requete : "+liste.getRequete().getRelevants().keySet());
			//System.out.println("les doc pertinents modele : "+ranking.keySet());
			hm.put(requete.getId(), mesure.eval(liste));
		}
		ArrayList<ArrayList<Double>> matriceNote = new ArrayList<ArrayList<Double>> ();
		
		//double[] moyenne = new double[10];						
		for (Entry<String, ArrayList<Double>> entree : hm.entrySet()) {		
			matriceNote.add(entree.getValue());
		}
		//System.out.println("la matrice de note "+matriceNote);
		int taille = matriceNote.size();
		//calcul de la moyenne 
		moyenne = new ArrayList<Double>();
		//initialisation du tableau moyenne 
		for(int n = 0 ; n < matriceNote.get(0).size() ; n++)
			moyenne.add(0.0);
		//affectation des notes
		for(int i =0 ; i < matriceNote.size() ; i++){
			ArrayList<Double> tmp = matriceNote.get(i);
			for(int j = 0 ; j < tmp.size() ; j++){
				moyenne.set(j,tmp.get(j) + moyenne.get(j));
			}
		}
		for(int i = 0 ; i < moyenne.size() ; i++ )
			moyenne.set(i,moyenne.get(i) / taille);
		
		this.variance = getVariance(moyenne,matriceNote);
		this.std = getStdDev(variance); 
	}	
	private ArrayList<Double> getVariance(ArrayList<Double>moyenne,ArrayList<ArrayList<Double>> matriceNote){
		ArrayList<Double> variance = new ArrayList<Double>();
		//initialisation du tableau
		for(int n = 0; n < moyenne.size() ; n++)
			variance.add(0.0);
		for (int i = 0 ; i < moyenne.size() ; i++){
			double tmp = 0;
			for(int j = 0 ; j < matriceNote.size() ; j++){
				tmp += (moyenne.get(i) - matriceNote.get(j).get(i)) * (moyenne.get(i) - matriceNote.get(j).get(i));
			}
			variance.set(i,tmp / matriceNote.size());
		}
		return variance;
	}
	
	private ArrayList<Double> getStdDev(ArrayList<Double> variance){
    	ArrayList<Double> std = new ArrayList<Double>();
    	for(int i=0; i<variance.size(); i++){
    		std.add(Math.sqrt(variance.get(i)));
    	}
    	return std;
    }

	public ArrayList<Double> getMoyenne() {
		return this.moyenne;
	}
	
	public ArrayList<Double> getVariance(){
		return this.variance;
	}
	
	public ArrayList<Double> getStd(){
		return this.std;
	}
}
