package modeles;

import indexation.Index;
import indexation.MyRandomAccessFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

public class CombinaisonLineaire extends MetaModel<Integer>{
	private HashMap<String, Object[]> queries;
	private HashMap<String, Object[]> train;
	private HashMap<String, Object[]> test;
	private ArrayList<Double> theta;
	private HashMap<String,Double> Y;
	private boolean fit;
	private String name;
	private double lambda,alpha;
	private int tmax;
	public CombinaisonLineaire(Index idx) {
		super(idx);
	}
	public CombinaisonLineaire(Index idx, HashMap<String, Object[]> queries, FeaturersList liste) {
		super(idx);
		this.list = liste;
		this.queries = queries;
	}
	public CombinaisonLineaire(boolean fit, Index idx, HashMap<String, Object[]> queries, FeaturersList liste, String name, double alpha
			,int tmax, double lambda) {
		super(idx);
		this.list = liste;
		this.queries = queries;
		this.name = name;
		this.lambda = lambda;
		this.alpha = alpha;
		this.tmax = tmax;
		this.theta = new ArrayList<Double>();
		this.fit = fit;
	}
	@Override
	public HashMap<Integer, Double> getScores(HashMap<String, Integer> query) {
		getTestTrain(0.8);
		String q = setQuery(query);		
		if(fit){
			fit(alpha, tmax, lambda);						
			//Y = new HashMap<String,Double>();
			//getY();.
			serialize();
		}else{
			deserialize();
		}		
		System.out.println(theta);
		int documents = idx.getDocs().size();		
		HashMap<Integer,Double> scoresDocs = new HashMap<Integer,Double>();		
		for(int j = 1 ; j < documents + 1 ; j++){		
			System.out.println(j);			
			double score = fTheta2(String.valueOf(j), q, theta);			
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
	public void fit(double alpha, int tmax, double lambda){
		getRandomTheta(list.getFeaturers().size());		
		for(int i=1; i<=tmax; i++){
			//Tirage aléatoire d’une requête q;
			String q				= getRandomQuery(train);
			//Tirage aléatoire d’un document pertinent d et d’un document non pertinent d' pour cette requête;
			ArrayList<String> ds 	= getTwoDocuments(q);
			String d 				= ds.get(0);
			if(d.equals("")) continue;
			String dPrime 			= ds.get(1);
			System.out.println("d = "+d+", d' = "+dPrime);
			//Si 1 − fθ(d, q) + fθ(d', q) > 0 alors on modifie le vecteur θ:
			if(1 - fTheta(d,q,theta) + fTheta(dPrime,q,theta) > 0){
				//θ ← θ + α( x(d,q) − x(d',q) )
				theta = ModifierVecteur(d,dPrime,q,alpha,theta);
			}
			// θ ← (1 − 2αλ)θ
			theta = mult(theta, (1-2*alpha*lambda));			
			System.out.println(theta);	
		}
	}
	public double error(){
		double error=0;
		for(int i=1; i<=10; i++){
			//Tirage aléatoire d’une requête q;
			String q				= getRandomQuery(test);
			//Tirage aléatoire d’un document pertinent d et d’un document non pertinent d' pour cette requête;
			ArrayList<String> ds 	= getTwoDocuments(q);
			String d 				= ds.get(0);
			if(d.equals("")) continue;
			error += Math.pow(fTheta(d, q, theta) - Y.get(q), 2);
			System.out.println(error);
		}
		return error;
	}
	private String getRandomQuery(HashMap<String, Object[]> data){
		while(true){
			Random generator = new Random();
			Object[] keys = data.keySet().toArray();			
			int r = generator.nextInt(keys.length);
			if(data.get(keys[r]).length>0){
				return (String) keys[r];
			}
			continue;
		}
	}
	private ArrayList<String> getTwoDocuments(String query){
		ArrayList<String> ds = new ArrayList<String>();
		Object[] r = queries.get(query);
		/* choisir un random parmis les documents pertinents de la requete*/
		Random generator = new Random();		
		String d = (String) r[generator.nextInt(r.length)];
		ds.add(d);
		/* choisir un random parmis les documents NON pertinents de la requete*/
		HashMap<Integer,String> hm = idx.getDocFrom();
		do{
			Object[] k = hm.keySet().toArray();
			Integer dp = (Integer) k[generator.nextInt(k.length)];
			if (!ArrayContains(r,dp)){
				ds.add(String.valueOf(dp));
				break;
			}
		}while(true);		
		return ds;
	}
	/**
	 * @return the fit
	 */
	public boolean isFit() {
		return fit;
	}
	/**
	 * @param fit the fit to set
	 */
	public void setFit(boolean fit) {
		this.fit = fit;
	}
	private boolean ArrayContains(Object[] k, Integer v){
		for(int i=0; i<k.length; i++){
			if(k[i] == v)
				return true;
		}
		return false;
	}
	private double fTheta(String d, String q, ArrayList<Double> theta){
		ArrayList<Double> xdq = new ArrayList<Double>(); 
		for(Featurer f : list.getFeaturers()){				
			xdq.add(f.getFeatures(d, q).get(0));
		}		
		return ProduitVectoriel(xdq,theta);
	}
	private double fTheta2(String d, String q, ArrayList<Double> theta){
		double s = 0;
		int i=0;
		for(Featurer f : list.getFeaturers()){			
			s += f.getFeatures(d, q).get(0)*theta.get(i++);
		}		
		return s;
	}
	private ArrayList<Double> mult(ArrayList<Double> theta, double d){
		for(int i=0; i<theta.size(); i++){
			theta.set(i,theta.get(i)*d);
		}
		return theta;
	}
	private void getRandomTheta(int n){
		theta = new ArrayList<Double>();
		for(int i=0; i<n; i++){
			theta.add(Math.random());	
		}		
	}
	private double ProduitVectoriel(ArrayList<Double> v1,ArrayList<Double> v2){
		double s=0;
		for(int i=0; i<v1.size(); i++){			
			s += v1.get(i)*v2.get(i);
		}
		return s;
	}
	private ArrayList<Double> ModifierVecteur(String d, String dPrime, String q, double alpha,ArrayList<Double> theta){
		ArrayList<Featurer> lf = list.getFeaturers();		
		for(int i=0; i<lf.size(); i++){	
			theta.set(i, theta.get(i) + alpha*(lf.get(i).getFeatures(d, q).get(0) - lf.get(i).getFeatures(dPrime, q).get(0)) );
		}						
		return theta;
	}
	private void getTestTrain(double pourcentage){
		train = new HashMap<String, Object[]>();
		test = new HashMap<String, Object[]>();
		for(String key : queries.keySet()){
			if(Math.random()<pourcentage){
				train.put(key, queries.get(key));
			}else{
				test.put(key, queries.get(key));
			}
		}
	}
	private void getY(){
		System.out.println("get Y");
		int i = 0;
		for(String q : queries.keySet()){
			System.out.println(i++);
			Object[] pert = queries.get(q);
			String p =(String)pert[0];
			if(p!=""){
				String d = p;
				double max = -1;		
				for(Featurer f : list.getFeaturers()){						
					ArrayList<Double> a = f.getFeatures(d, q);
					if(max<a.get(0)) max = a.get(0);
				}
				Y.put(q, max);
			}
		}		
	}
	private void serialize(){
		MyRandomAccessFile d 	= new MyRandomAccessFile(name+"_SerialiseTheta.txt");
		String strTheta 		= ArrayListToString(theta.toString());
		//MyRandomAccessFile y 	= new MyRandomAccessFile(name+"_SerialiseY.txt");
		//String strY		 		= idx.HMToString(Y.toString());
		try {
			d.writeToFile(strTheta, 0);			
			//y.writeToFile(strY, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void deserialize(){
		String d  = idx.readFile(name+"_SerialiseTheta.txt");
		strToThetaArrayList(d);
	}
	private String ArrayListToString(String a){
		String ret=a.replaceAll(" ","");
		ret = ret.replaceAll("\\[","");
		ret = ret.replaceAll("\\]","");
		return ret;
	}
	private void strToThetaArrayList(String t){
		String[] s = t.split(",");
		for(int i=0; i<s.length; i++){			
			theta.add(Double.parseDouble(s[i]));
		}
	}
	private String setQuery(HashMap<String, Integer> q){
		String query = "";
		for(String s : q.keySet()){
			query += s+" ";
		}
		return query;
	}
}
