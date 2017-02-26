package modeles;

import java.util.ArrayList;
import java.util.HashMap;

import indexation.Index;

public abstract class RandomWalk {
	public abstract HashMap<String,Double> marcheAleatoire(HashMap<String,String> suc,HashMap<String,String> pred);	
}
