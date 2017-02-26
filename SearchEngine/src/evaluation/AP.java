package evaluation;

import java.util.ArrayList;

public class AP extends EvalMeasure{

	@Override
	public ArrayList<Double> eval(IRList l) {
		ArrayList<Double> toRet = new ArrayList<Double>();
		int nbRel = l.getRequete().getRelevants().size(); // nb_pertinants	
		int nbRelRet = 0;
		Double AP = 0.;
		ArrayList<Integer> rankingDocs = new ArrayList<Integer>(l.getDocuments().keySet());
		for (int i = 0; i < rankingDocs.size(); i++) {
			Integer id_doc = rankingDocs.get(i);
			boolean docIsRel = l.getRequete().isRelevants(String.valueOf(id_doc));
			if (docIsRel) {
				nbRelRet++;
				double precision_i = nbRelRet * 1. / (i+1);
				AP += precision_i;
			}
		}
		toRet.add(AP/nbRel);
		return toRet;
	}	
}
