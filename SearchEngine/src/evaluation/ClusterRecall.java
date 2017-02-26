package evaluation;

import java.util.ArrayList;

public class ClusterRecall extends EvalMeasure{
	private int n;

	public ClusterRecall(int n){
		this.n = n;
	}
	public ClusterRecall(){
		this(20);
	}
	@Override
	public ArrayList<Double> eval(IRList l) {
		double subTopFound = 0.;
		ArrayList<String> subTopRet = new ArrayList<String>();
		ArrayList<Double> res = new ArrayList<Double>();
		ArrayList<Integer> Docs = new ArrayList<Integer>(l.getDocuments().keySet());
		Query q = l.getRequete();
		for(int i = 0 ; i < this.n && i < Docs.size() ; i++){
			if(l.getRequete().isRelevants(String.valueOf(Docs.get(i)))){
				if(!subTopRet.contains(q.getRelevants().get(String.valueOf(Docs.get(i))))){
					subTopFound++;
					subTopRet.add(q.getRelevants().get(String.valueOf(Docs.get(i))));
				}
			}
		}
		//System.out.println(subTopRet);
		double crn = subTopFound / ((QueryDiv)l.getRequete()).getNbSubTopics();
		//System.out.println("nb de sous topics pour les documents de la requete "+((QueryDiv)l.getRequete()).getNbSubTopics());
		res.add(crn);
		return res;

	}
}
