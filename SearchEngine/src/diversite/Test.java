package diversite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import evaluation.Query;
import evaluation.QueryDiv;
import evaluation.QueryParserDiv;
import indexation.Index;
import indexation.Parser;
import indexation.ParserCISI_CACM;
import indexation.Stemmer;
import modeles.IRmodel;
import modeles.LanguageModel;
import modeles.Okapi;
import modeles.ValueComparator;
import modeles.Vectoriel;
import modeles.W2;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;


public class Test {

	private Index idx;
	private HashMap<String, Integer> indexOfStem = new HashMap<String,Integer>();
	private HashMap<Integer,Integer> indexDocs = new HashMap<Integer,Integer>();
	private IRmodel model;
	private int nFirst;

	public Test(Index idx,IRmodel model,int nFirst){
		this.idx = idx;
		this.model = model;
		this.nFirst = nFirst;
		int i = 0;
		for (String stem : this.idx.getListStem()){
			this.indexOfStem.put(stem, i);
			i = i + 1;
		}
	}

	public ArrayList<int[]> getRanking(HashMap<String,Integer> q){
		ArrayList<int[]> liste = new ArrayList<int[]>();
		Map<Integer,Double> ranking = model.getRanking(q);
		int cpt = 0;
		//Creation du dataset
		int taille =  this.indexOfStem.size();
		for(Integer doc : ranking.keySet()){
			this.indexDocs.put(cpt, doc);
			if(cpt >= this.nFirst)
				break;
			cpt++;
			int[] tab = new int[ taille + 1];
			for (String stem : this.idx.getTfsForDoc(doc).keySet()){
				tab[indexOfStem.get(stem)] = this.idx.getTfsForDoc(doc).get(stem);
			}
			tab[taille] = doc;
			liste.add(tab);
		}
		return liste;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		Parser parser = new ParserCISI_CACM();
		Stemmer representer = new Stemmer();
		Index idx = new Index("easyCLEF08",parser,representer);
		boolean flag = true;
		if(flag){
			idx.indexation("easyCLEF08_text.txt");
			idx.serialize();
		}else{
			idx.deserialize();
		}
		//initialisation des modeles
		W2 w2 = new W2(idx);
		Vectoriel v = new Vectoriel(w2,idx,true);
		Okapi oka = new Okapi(idx);
		IRmodel ml = new LanguageModel(idx);
		QueryParserDiv qpd = new QueryParserDiv();
		ArrayList<Integer> listDocs = idx.getListDocs();

		qpd.init("easyCLEF08_query.txt","easyCLEF08_gt.txt");
		ArrayList<Query> listQd = new ArrayList<Query>();
		//Charement des requetes
		QueryDiv qd = qpd.nextQueryDiv();
		while(qd != null){
			listQd.add(qd);
			qd = qpd.nextQueryDiv();
		}
		Test t = new Test(idx,v,40);
		int cpt = 1;
		for(Query q : listQd){
			HashMap<String,Integer> query = representer.getTextRepresentation(q.getText());
			ArrayList<int[]> tab = t.getRanking(query);
			FileWriter fw = new FileWriter("Resultats/resultat_"+cpt+".csv", false);
			BufferedWriter output = new BufferedWriter(fw);
			for(int j = 0 ; j < tab.get(0).length ; j++){
				if(j == tab.get(0).length - 1)
					output.write(j+"");
				else
					output.write(j+";");
			}
			output.write("\n");
			for(int[] array : tab){
				for(int i = 0 ; i < array.length ; i++){
					if(i == array.length -1)
						output.write(array[i]+"");
					else
						output.write(array[i]+";");
				}
				output.write("\n");
			}
			output.flush();
			output.close();
			cpt++;
		}
		
		
		
	}
}
