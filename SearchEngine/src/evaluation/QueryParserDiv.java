package evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indexation.Document;
import indexation.ParserCISI_CACM;

public class QueryParserDiv extends ParserCISI_CACM{
	private static final long serialVersionUID = 1L;
	private String filenameRel;
	private HashMap<String,HashMap<String,String>> rels = new HashMap<String,HashMap<String,String>>(); 

	public QueryDiv nextQueryDiv() throws NullPointerException{
		Document d = nextDocument();
		if(d != null){			
			String id = d.getId();
			HashMap<String,String> docs = rels.get(id);			
			return new QueryDiv(d,docs);
		}else return null;
	}	

	public void init(String filenameQuery, String filenameRel){
		try {
			files				= getAllFiles(new File(filenameQuery));			
			curF				= 0;
			br					= new RandomAccessFile(files.get(curF),"r");
			this.filenameRel 	= filenameRel;
			readRels();
			//System.out.println("Initialisé correctement !");
		}		
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public void initRel(String filenameRel){
		this.filenameRel 	= filenameRel;
		readRels();
	}
	//@SuppressWarnings("resource")
	private void readRels(){ // Sans prendre en compte les 2 derniers paramètres !!!!!!
		//rels = new ArrayList<ArrayList<String[]>>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filenameRel));
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("#") || line.length() < 2)
					continue;
				String[] l = line.split(" ");
				if(l[0].startsWith("0"))
					l[0] = l[0].substring(1);
				if(!rels.containsKey(l[0])){
					HashMap<String,String> tmp = new HashMap<String,String>();
					tmp.put(l[1],l[3]);
					rels.put(l[0],tmp);
				}else{
					rels.get(l[0]).put(l[1],l[3]);
					
				}

			}			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO");
		}			    		
	}
	public HashMap<String,HashMap<String,String>> getRels(){
		return rels;
	}

}
