package diversite;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import indexation.Index;
import modeles.IRmodel;
import modeles.ValueComparator;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.NormalizedEuclideanDistance;

public class Clustering {
	private Index idx;
	private Dataset dataset;
	private Dataset[] clusters;
	private HashMap<String, Integer> indexOfStem = new HashMap<String,Integer>();
	private HashMap<Integer,Integer> indexDocs = new HashMap<Integer,Integer>();
	private IRmodel model;
	private int nFirst;
	 
	public Clustering(Index idx,IRmodel model,int nFirst){
		this.idx = idx;
		this.dataset = new DefaultDataset();
		this.model = model;
		this.nFirst = nFirst;
		int i = 0;
		for (String stem : this.idx.getListStem()){
			this.indexOfStem.put(stem, i);
			i = i + 1;
		}
	}

	
	public Dataset getDataset(){
		return this.dataset;
	}
	
	
	public Dataset[] setClusters(Clusterer clusterer){
		DistanceMeasure dm = new NormalizedEuclideanDistance(this.dataset);
		
		
		return this.clusters;
	}
	
	//Document ordonnés par similarite a la requete : ranking
	//faire une clustering des N premiers documents : nFirst
	public LinkedHashMap<Integer, Double> getRanking(HashMap<String,Integer> q){
		Map<Integer,Double> ranking = model.getRanking(q);
		int cpt = 0;
		//Creation du dataset
		for(Integer doc : ranking.keySet()){
			this.indexDocs.put(cpt, doc);
			if(cpt >= this.nFirst)
				break;
			cpt++;
			Instance inst = new SparseInstance(idx.getStems().size());
			for (String stem : this.idx.getTfsForDoc(doc).keySet()){
				inst.put(this.indexOfStem.get(stem), (double)(this.idx.getTfsForDoc(doc).get(stem)));
			}
			this.dataset.add(inst);
		}
		//Algorithme de clustering : Kmedoids
		//DistanceMeasure dm = new NormalizedEuclideanDistance(this.dataset);
		Clusterer kmedo = new KMeans(3);
		System.out.println(this.dataset);
		Dataset[] cluster = kmedo.cluster(this.dataset);
		
		LinkedHashMap<Integer, Double> rank = new LinkedHashMap<Integer, Double>();
		
		HashMap<Integer,Double> orderCluster = new HashMap<Integer,Double>();
		for(int i = 0 ; i < cluster.length ; i++){
			orderCluster.put(i, (double)(cluster[i].toArray().length));
		}
		//System.out.println("ok");
		ValueComparator comparateur = new ValueComparator(orderCluster);
		TreeMap<Integer,Double> mapTriee = new TreeMap<Integer,Double>(comparateur);
		mapTriee.putAll(orderCluster);
		Set<Integer> indiceCluster = mapTriee.keySet();
		
		
		/*for(Integer ind : indiceCluster){
			System.out.println("cluster avec indice "+ind);
			for(int i = 0 ; i < cluster[ind].toArray().length ; i++){
				System.out.println(((Instance)(cluster[ind].toArray()[i])).getID());
			}
		}*/
		
		System.out.println("fiezjoizehfio");
		for(int i = 0 ; i < this.nFirst ; i++){
			for(Integer ind : indiceCluster){
				if(cluster[ind].toArray().length > i){
					int index = ((Instance)(cluster[ind].toArray()[i])).getID();
					rank.put(this.indexDocs.get(index), ranking.get(this.indexDocs.get(index)));
				}
			}
		}
		return rank;
	}

}
