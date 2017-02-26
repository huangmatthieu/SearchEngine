package evaluation;

import java.util.ArrayList;

public class Precision extends EvalMeasure {
	private int n;
	public Precision(int n){
		this.n = n;
	}
	public Precision(){
		this(20);
	}

	@Override
	public ArrayList<Double> eval(IRList l) {
		ArrayList<Integer> rankingDocs = new ArrayList<Integer>(l.getDocuments().keySet());	
		ArrayList<Double> precision = new ArrayList<Double>();
		int nbDocs = rankingDocs.size();
		int nbRelRet = 0 ;
		int nbRet = 0;
		for(int i = 0 ; i < this.n && i < nbDocs ; i ++){
			nbRet += 1;
			if(l.getRequete().isRelevants(String.valueOf(rankingDocs.get(i)))){
				nbRelRet += 1;
			}
		}
		precision.add(1. * nbRelRet / nbRet);
		return precision;
		
	}

}
