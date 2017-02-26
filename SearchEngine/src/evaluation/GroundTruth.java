package evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GroundTruth {

	public static HashMap<Integer,Integer> mapDocSubTop(String filename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(String line; (line = br.readLine()) != null; ) {
			if(line.startsWith("#") || line.length() < 2)
				continue;
			String[] tab = line.split(" ");
			int numdoc = Integer.parseInt(tab[1]);
			int subTop = Integer.parseInt(tab[3]);
			map.put(numdoc, subTop);
		}
		br.close();
		return map;
	}
	
	public static int getNbSubTopic(String filename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		int cpt = 0;
		ArrayList<Integer> contain = new ArrayList<Integer>();
		for(String line; (line = br.readLine()) != null; ) {
			if(line.startsWith("#") || line.length() < 2)
				continue;
			String[] tab = line.split(" ");
			int subTop = Integer.parseInt(tab[3]);
			if(!contain.contains(subTop)){
				cpt++;
				contain.add(subTop);
			}
		}
		br.close();
		return cpt;
	}

}
