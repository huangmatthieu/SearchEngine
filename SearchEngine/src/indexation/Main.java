package indexation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import diversite.*;
import evaluation.*;
import modeles.*;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.NormalizedEuclideanDistance;


public class Main {	
	public static void main(String[] argc) throws FileNotFoundException, IOException, ClassNotFoundException{
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

		//System.out.println(GroundTruth.getNbSubTopic("easy235_gt.txt"));
		//Initialisation des mesures
		EvalMeasure p = new Precision();
		EvalMeasure cr = new ClusterRecall();
		EvalMeasure ap = new AP();
		ArrayList<Double> resultatPrecision= new ArrayList<Double>();
		ArrayList<Double> resultatCR= new ArrayList<Double>();
		//Evaluation algorithme glouton
		/*long startTime = System.currentTimeMillis();
		for(int k = 0 ; k <= 10 ; k++){
			double precision = 0;
			double crmean = 0;
			Value mmr = new ValueMMR(idx,(k * 0.1));
			Greedy gr = new Greedy(listDocs,21,mmr);
			for(int i = 0 ; i < listQd.size() ; i++){
				Map<Integer,Double> ranking = gr.getRanking(representer.getTextRepresentation(listQd.get(i).getText()));
				IRList irld = new IRListDiv(listQd.get(i),ranking);
				precision += p.eval(irld).get(0);
				crmean += cr.eval(irld).get(0);
			}
			precision /= listQd.size();
			crmean /= listQd.size();
			resultatPrecision.add(precision);
			resultatCR.add(crmean);
			System.out.println("Précision moyenne : Algorithme Glouton MMR alpha = "+(k * 0.1)+" : "+precision);
			System.out.println("CR moyen : Algorithme Glouton MMR alpha = "+(k * 0.1)+" : "+crmean);
		}
		System.out.println(resultatPrecision);
		System.out.println(resultatCR);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("\n\nTemps d'execution :"+elapsedTime+" ms");*/
		
		ArrayList<Double> resPrec = new ArrayList<Double>();
		ArrayList<Double> resCR = new ArrayList<Double>();
		for(int z = 3 ; z < 16 ; z++){
			double precision = 0;
			double crmean = 0;
			for(int i = 0 ; i < listQd.size() ; i++){
				BufferedReader br = new BufferedReader(new FileReader("Rankings/Rankings"+z+"/ranking"+(1+i)+".txt"));
				String line = br.readLine();
				String[] tab = line.split(",");
				LinkedHashMap<Integer,Double> ranking = new LinkedHashMap<Integer,Double>();
				for(int j = 0 ; j < tab.length ; j++)
					ranking.put(Integer.parseInt(tab[j].trim()), 1.0);
				//System.out.println(ranking);
				
				IRList irld = new IRListDiv(listQd.get(i),ranking);
				precision += p.eval(irld).get(0);
				crmean += cr.eval(irld).get(0);
				br.close();
			}
			precision /= listQd.size();
			crmean /= listQd.size();
			resPrec.add(precision);
			resCR.add(crmean);
		}
		System.out.println(resPrec);
		System.out.println(resCR);
		

		EvalIRModel evalModelPrec2 = new EvalIRModel(v,p,listQd);
		EvalIRModel evalModelCR2 = new EvalIRModel(v,cr,listQd);
		EvalIRModel evalModelAP2 = new EvalIRModel(v,ap,listQd);

		EvalIRModel evalModelPrec = new EvalIRModel(ml,p,listQd);
		EvalIRModel evalModelCR = new EvalIRModel(ml,cr,listQd);
		EvalIRModel evalModelAP = new EvalIRModel(ml,ap,listQd);

		EvalIRModel evalModelPrec3 = new EvalIRModel(oka,p,listQd);
		EvalIRModel evalModelCR3 = new EvalIRModel(oka,cr,listQd);
		EvalIRModel evalModelAP3 = new EvalIRModel(oka,ap,listQd);
		//evaluation
		
		/*System.out.println("\nmodele vectoriel");
		evalModelPrec2.evaluer();
		evalModelCR2.evaluer();
		evalModelAP2.evaluer();
		System.out.println("moyenne P@20 : "+evalModelPrec2.getMoyenne().get(0));
		System.out.println("moyenne CR@20 : "+evalModelCR2.getMoyenne().get(0));
		System.out.println("moyenne AP : "+evalModelAP2.getMoyenne().get(0));
		
		System.out.println("modele langue");
		evalModelPrec.evaluer();
		evalModelCR.evaluer();
		evalModelAP.evaluer();
		System.out.println("moyenne P@20 : "+evalModelPrec.getMoyenne().get(0));
		System.out.println("moyenne CR@20 : "+evalModelCR.getMoyenne().get(0));
		System.out.println("moyenne AP : "+evalModelAP.getMoyenne().get(0));

		System.out.println("\nmodele okapi");
		evalModelPrec3.evaluer();
		evalModelCR3.evaluer();
		evalModelAP3.evaluer();
		System.out.println("moyenne P@20 : "+evalModelPrec3.getMoyenne().get(0));
		System.out.println("moyenne CR@20 : "+evalModelCR3.getMoyenne().get(0));
		System.out.println("moyenne AP : "+evalModelAP3.getMoyenne().get(0)); */
		
		HashMap<String,Integer> query = representer.getTextRepresentation(listQd.get(0).getText());
		Map<Integer,Double> ranking2 = v.getRanking(query);
		/*Clustering clust = new Clustering(idx,v,21);
		Map<Integer,Double> ranking = clust.getRanking(query);
		System.out.println(ranking);
		System.out.println(ranking2);
		IRList irld = new IRListDiv(listQd.get(0),ranking);
		double precision = p.eval(irld).get(0);
		double crmean = cr.eval(irld).get(0);
		System.out.println(precision + "   " +crmean); */



	}
}
