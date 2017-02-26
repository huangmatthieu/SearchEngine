package evaluation;

import indexation.Document;
import indexation.MyRandomAccessFile;
import indexation.Parser;
import indexation.ParserCISI_CACM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class QueryParser extends ParserCISI_CACM{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filenameRel;
	private HashMap<String,String> rels;
	
	public Query nextQuery() throws NullPointerException{
		Document d = nextDocument();
		if(d != null){			
			String id 	= d.getId();
			String docs = "";
			if(id.length()==1) id = "0"+id;
			if(rels.containsKey(id))
				docs = rels.get(id);			
			return new Query(d,docs);
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
		rels = new HashMap<String,String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filenameRel));
			for(String line; (line = br.readLine()) != null; ) {
		        String[] l = line.split(" ");
		        if(!rels.containsKey(l[0]))
		        	rels.put(l[0], l[1]);
		        else rels.put(l[0], rels.get(l[0])+","+l[1]);
		    }			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO");
		}			    		
	}
	public HashMap<String,String> getRels(){
		return rels;
	}
	
}

