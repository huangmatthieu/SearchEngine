package modeles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import indexation.Index;

public abstract class Featurer {
	protected Index idx;
	protected HashMap<String, HashMap<Integer, Double>> features; 
	//retourne une liste de scores (possiblement unique selon les cas) pour le document idDoc et la	requête query.
	public abstract ArrayList<Double> getFeatures(String idDoc, String query);
	public abstract ArrayList<Double> getFeatures(String idDoc);
}
