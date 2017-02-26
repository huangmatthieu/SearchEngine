package evaluation;

import indexation.Index;
import indexation.Main;
import indexation.ParserCISI_CACM;
import indexation.Stemmer;

import java.util.ArrayList;

import modeles.IRmodel;
import modeles.LanguageModel;
import evaluation.AP;
import evaluation.EvalIRModel;
import evaluation.EvalMeasure;
import evaluation.PrecisionRappel;
import evaluation.Query;
import evaluation.QueryParser;

public class GridSearch {
	private IRmodel model;
	private EvalMeasure mesure;
	private ArrayList<Query> queries;
	
	public GridSearch(IRmodel model, EvalMeasure mesure, ArrayList<Query> queries){
		this.model = model;
		this.mesure = mesure;
		this.queries = queries;
	}

	/**
	 * @return the model
	 */
	public IRmodel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(IRmodel model) {
		this.model = model;
	}

	/**
	 * @return the mesure
	 */
	public EvalMeasure getMesure() {
		return mesure;
	}

	/**
	 * @param mesure the mesure to set
	 */
	public void setMesure(EvalMeasure mesure) {
		this.mesure = mesure;
	}

	/**
	 * @return the queries
	 */
	public ArrayList<Query> getQueries() {
		return queries;
	}

	/**
	 * @param queries the queries to set
	 */
	public void setQueries(ArrayList<Query> queries) {
		this.queries = queries;
	}
	
	public ArrayList<Double> searchBest(ArrayList<ArrayList<Double>> setParams){
		ArrayList<Double> best = new ArrayList<Double>();
		double max=0;
		for(ArrayList<Double> entry : setParams){
			double score = 0;
			this.model.setParams(entry);
			EvalIRModel evalModel = new EvalIRModel(this.model, this.mesure, this.queries);
			evalModel.evaluer();
			ArrayList<Double> listeMoy = evalModel.getMoyenne();
			for(Double val : listeMoy){
				score += val;
			}
			score /= listeMoy.size();
			if(score > max){
				best = entry;
				max = score;
			}
		}
 		return best;
	}
	
	public static void main(String[] args){
		Stemmer stemmer			= new Stemmer();
		ParserCISI_CACM parser 	= new ParserCISI_CACM();			
		Index i 				= new Index("cacm", parser, stemmer);		
		//Main.init("cacm",false,stemmer,parser,i);
		QueryParser parserQuery = new QueryParser();								
		parserQuery.init("cacm.qry","cacm.rel");
		Query temp = parserQuery.nextQuery();	
		ArrayList<Query> queries = new ArrayList<Query>();
		ArrayList<Query> trainQueries = new ArrayList<Query>();
		ArrayList<Query> testQueries = new ArrayList<Query>();
		while(temp!=null){				
			queries.add(temp);
			temp 		= parserQuery.nextQuery();
		}
		int sizeQueries = queries.size();
		for(int j = 0 ; j < sizeQueries ; j++){
			if(Math.random() < 0.7){
				trainQueries.add(queries.get(j));
			}else{
				testQueries.add(queries.get(j));
			}
		}
		ArrayList<Query> list = new ArrayList<Query>();
		list.add(queries.get(0));
		list.add(queries.get(1));
		System.out.println("GridSearch sur le modele de langue avec lambda variant de 0 à 1 par pas de 0.1");
		IRmodel modelLangue = new LanguageModel(i);
		EvalMeasure ap = new PrecisionRappel(4);
		ArrayList<ArrayList<Double>> params = new ArrayList<ArrayList<Double>>();
		for (double cpt = 0.0; cpt <= 0.2; cpt+=0.1){
			ArrayList<Double> val = new ArrayList<Double>();
			val.add(cpt);
			params.add(val);
		}
		System.out.println(params);
		//GridSearch gsml= new GridSearch(modelLangue,ap,list);
		GridSearch gsml= new GridSearch(modelLangue,ap,trainQueries);
		ArrayList<Double> best = gsml.searchBest(params);
		System.out.println("best parametre : "+best);
		
	}

}
