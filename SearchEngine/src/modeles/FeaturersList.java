package modeles;

import java.util.ArrayList;

public class FeaturersList {
	private ArrayList<Featurer> featurers;
	public FeaturersList(){
		featurers = new ArrayList<Featurer>();
	}
	public void add(Featurer f){
		featurers.add(f);
	}
	public ArrayList<Featurer> getFeaturers(){ return featurers;}
}
