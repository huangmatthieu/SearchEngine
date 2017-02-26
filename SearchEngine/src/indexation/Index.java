package indexation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Index {
	private String fichierSource;
	private String name; // variable contenant le nom de lâ€™index;
	private MyRandomAccessFile index;// flux RAF ouvert en lecture/Ã©criture sur le fichier dâ€™index nommÃ© name+â€œ_indexâ€�;
	private MyRandomAccessFile inverted;// flux RAF ouvert en lecture/Ã©criture sur le fichier dâ€™index inversÃ© nommÃ© name+â€œ_invertedâ€�;
	private HashMap<Integer,String> docs;// table de hashage contenant pour chaque document la position et la longueur
	//de sa reprÃ©sentation (en octets) dans le fichier index (les clÃ©s de la table sont les ids des documents);
	private HashMap<String,String> stems;// table de hashage contenant pour chaque stem sa position et sa longueur
	//(en octets) dans le fichier inverted;
	private HashMap<Integer,String> docFrom;// table de hashage contenant pour chaque document son fichier source
	//ainsi que sa position et sa longueur en octets dans celui-ci.
	private Parser parser;// lâ€™objet Parser utilisÃ© pour traiter le fichier source;
	private Stemmer textRepresenter; //lâ€™objet de reprÃ©sentation de texte utilisÃ© (par exemple un objet Stemmer).
	private HashMap<String,String> predecesseurs;
	private HashMap<String,String> successeurs;
	private Set<String> listStem = new HashSet<String>();
	public Index(String name, Parser parser, Stemmer textRepresenter){
		this.name			= name;
		this.parser			= parser;
		this.textRepresenter= textRepresenter;
		docs 				= new HashMap<Integer,String>();
		stems 				= new HashMap<String,String>();
		docFrom				= new HashMap<Integer,String>();
		index 				= new MyRandomAccessFile(name+"_index.txt");
		inverted			= new MyRandomAccessFile(name+"_inverted.txt");
		predecesseurs 		= new HashMap<String,String>();
		successeurs 		= new HashMap<String,String>();
	}	
	//Une mÃ©thode indexation qui sâ€™occupe de crÃ©er les structures dâ€™index Ã  partir du fichier source de la collection Ã  indexer;
	public void indexation(String filename){
		//Initialisations
		parser.init(filename);
		fichierSource	= filename;
		HashMap<String,String> infos;
		int add;
		int positioni 	= 0;
		long dep	 	= 0;
		boolean first	= true;		
		Document temp 	= parser.nextDocument();			
		infos 			= new HashMap<String,String>();				
		while(temp!=null){
			String id 		= temp.getId();			
			try {
				// Ecrire pour chaque document son fichier d'origine			
				docFrom.put(Integer.parseInt(id), filename+":"+dep+":"+(parser.br.getFilePointer()-dep));
				dep = parser.br.getFilePointer()-1;				
				setPredecesseurs(temp.getLinks(),temp.getId());
				successeurs.put(temp.getId(),temp.getLinksForSucc());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, Integer> hm = textRepresenter.getTextRepresentation(temp.getText());
			Set<String> les_mots = hm.keySet();
			this.listStem.addAll(les_mots);
			// Inverted :
			// Parcourir hm pour l'indexation inversÃ©e
			//infos 						= ParcourirStems(hm, infos, id);
			// Parcourir hashmap
			Set<String> cles = hm.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()){
			   String stem = (String)it.next(); 
			   Integer tf = (Integer)hm.get(stem);
			   String newVal;
			   if(infos.containsKey(stem)){
				   newVal = infos.get(stem)+";"+id+":"+tf;
			   }else{
				   newVal = id+":"+tf;
			   }
			   infos.put(stem, newVal);			   
			}
			// Index : 
			// Ecrire dans le fichier d'indexation
			String data 				= writeIndex(hm, index, positioni);
			// decalage : virgule + premier char
			if(first){ add=0; first = false; }else add=0;
			// Ecrire dans la hashmap : (id=posDepart:posFin)
			int posFin 					= positioni+data.length();
			docs.put(Integer.parseInt(temp.getId()), (positioni+add)+":"+(posFin));
			// calcul de la position suivante
			positioni 					= posFin;
			// Document suivant
			temp 						= parser.nextDocument();
		}	
		// Calcul des positions pour l'indexation inversÃ©e
		calculPositions(infos);
		// Ecrire dans le fichier inverse
		writeInverted(infos, inverted);
	}	
	//Une mÃ©thode getTfsForDoc qui retourne la reprÃ©sentation (stem-tf) dâ€™un document Ã  partir de lâ€™index;
	public HashMap<String,Integer> getTfsForDoc(int id){
		int position,taille;
		String p 	= docs.get(id);		
		String[] p2 = p.split(":");
		position 	= Integer.parseInt(p2[0]);
		taille 		= Integer.parseInt(p2[1]) - position + 1;
		try {
			byte[] b = index.readFromFile(position, taille);
			String str = new String(b, StandardCharsets.UTF_8);
			return strToHm(str);
		} catch (IOException e) {
			return null;
		}
	}
	
	//Une mÃ©thode getTfsForStem qui retourne la reprÃ©sentation (doc-tf) dâ€™un stem Ã  partir de lâ€™index inversÃ©;
	public HashMap<Integer,Integer> getTfsForStem(String stem){
		int position,taille;
		String p 					= stems.get(stem);
		if(p!=null){
			String[] p2 				= p.split(":");
			position 					= Integer.parseInt(p2[0]);
			taille 						= Integer.parseInt(p2[1]);
			try {
				byte[] b 	= inverted.readFromFile(position, taille);
				return strToHm2(b);
			} catch (IOException e) {
				return null;
			}
		}else{
			HashMap<Integer,Integer> b= new HashMap<Integer,Integer>();
			b.put(0, 0);
			return b;
		}
		
	}	
	
	//Une mÃ©thode getStrDoc qui retourne la chaÃ®ne de caractÃ¨res dont est issu un document donnÃ© dans le fichier source.
	public String getStrDoc(int id){	
		String s = docFrom.get(id);
		String[] sp = s.split(":");
		int posDep = Integer.parseInt(sp[1]);
		int taille = Integer.parseInt(sp[2]);
		MyRandomAccessFile m = new MyRandomAccessFile(name+".txt");
		try {
			byte[] b = m.readFromFile(posDep, taille);
			String str = new String(b, StandardCharsets.UTF_8);
			Document d = parser.getDocument(str);	
			return d.getText();
		} catch (IOException e) {
			return "";
		}
		
	}
	
	// Fonctions de parcours
	private HashMap<String,String> ParcourirStems(HashMap<String, Integer> doc, HashMap<String,String> infos, String id){
		// Parcourir hashmap
		Set<String> cles = doc.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()){
		   String stem = (String)it.next(); 
		   Integer tf = (Integer)doc.get(stem);
		   String newVal;
		   if(infos.containsKey(stem)){
			   newVal = infos.get(stem)+";"+id+":"+tf;
		   }else{
			   newVal = id+":"+tf;
		   }
		   infos.put(stem, newVal);			   
		}
		return infos;
	}
	private int calculPositions(HashMap<String,String> infos){
		// Parcourir infos
		int position = 0;
		Set cles = infos.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
		   String stem = (String)it.next(); 
		   String desc = (String)infos.get(stem);
		   int l = stem.length() + desc.length() +1;
		   String posAndLength = position+":"+l;
		   //System.out.println(stem+" : "+posAndLength); try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		   stems.put(stem, posAndLength);			 
		   position+=l+1;			  
		}
		return position;
	}
	
	// Fonctions d'ecriture sur les fichiers
	private void writeInverted(HashMap<String,String> hm, MyRandomAccessFile fichier){
		String data = HMToString(hm.toString());			
		// Ecrire dans le fichier
		try {
			fichier.writeToFile(data, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String writeIndex(HashMap<String,Integer> hm, MyRandomAccessFile fichier, int position){
		String data = HMToString(hm.toString());			
		// Ecrire dans le fichier
		try {
			fichier.writeToFile(data, position);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	// Getters
	public HashMap<Integer,String> getDocs(){
		return docs;
	}
	public HashMap<String,String> getStems(){
		return stems;
	}
	public HashMap<Integer,String> getDocFrom(){ return docFrom;}
	public int getNbDocs(){
		return docs.size();
	}
	public int getStemsLength(){
		return stems.size();
	}
	// Fonctions de traitements de Strings
	private HashMap<String,Integer> strToHm(String str){
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] stemTf =  s[i].split("=");
			try{
				hm.put(stemTf[0], Integer.parseInt(stemTf[1]));
			}catch(ArrayIndexOutOfBoundsException e){ 
				//System.out.println("problem !");
			}			
		}
		return hm;
	}
	private HashMap<Integer,Integer> strToHm2(byte[] b){
		HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();					
		String str 					= new String(b, StandardCharsets.UTF_8);
		String[] p2					= str.split("=");
		p2							= p2[1].split(";");
		for(int i=0; i<p2.length; i++){ 
			String[] p3	= p2[i].split(":");
			int depart 	= Integer.parseInt(p3[0]);
			int size 	= Integer.parseInt(p3[1]);
			hm.put(depart, size);
		}			
		return hm;		
	}
	public static String HMToString(String hm){
		String ret=hm.replaceAll(" ","");
		ret = ret.replaceAll("\\{","");
		ret = ret.replaceAll("\\}","");
		ret+=",";
		return ret;
	}
	public String readFile(String filename){
		File file = new File(filename);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			return new String(data, "UTF-8");
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			return "";
		}				
	}
	private void strToDocsHm(String str){
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] s2	= s[i].split("=");
			docs.put(Integer.parseInt(s2[0]), s2[1]);
		}		
	}
	private void strToStemsHm(String str){
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] s2	= s[i].split("=");
			stems.put(s2[0], s2[1]);
		}		
	}
	private void strToDocFromHm(String str){
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] s2	= s[i].split("=");
			docFrom.put(Integer.parseInt(s2[0]), s2[1]);
		}		
	}
	private void strToPredecesseurs(String str){
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] s2	= s[i].split("=");
			predecesseurs.put(s2[0], s2[1]);
		}		
	}
	private void strToSucesseurs(String str){
		String[] s = str.split(",");
		for(int i=0; i<s.length; i++){
			String[] s2	= s[i].split("=");
			if(s2.length == 1)
				continue;
			successeurs.put(s2[0], s2[1]);
		}		
	}
	// Serialisation
	public void serialize() throws FileNotFoundException, IOException{
		MyRandomAccessFile d 	= new MyRandomAccessFile(name+"_SerialiseDocs.txt");
		MyRandomAccessFile s 	= new MyRandomAccessFile(name+"_SerialiseStems.txt");
		MyRandomAccessFile df 	= new MyRandomAccessFile(name+"_SerialiseDocFrom.txt");
		MyRandomAccessFile pre 	= new MyRandomAccessFile(name+"_SerialisePrec.txt");
		MyRandomAccessFile suc 	= new MyRandomAccessFile(name+"_SerialiseSucc.txt");
		String strDocs 			= HMToString(docs.toString());
		String strStems 		= HMToString(stems.toString());
		String strDocFrom 		= HMToString(docFrom.toString());
		String predecess 		= HMToString(predecesseurs.toString());
		String sucess 			= HMToString(successeurs.toString());
		try {
			d.writeToFile(strDocs, 0);
			s.writeToFile(strStems, 0);
			df.writeToFile(strDocFrom, 0);
			pre.writeToFile(predecess, 0);
			suc.writeToFile(sucess, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File fichier =  new File("listStem.ser") ;
		ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(fichier)) ;
		oos.writeObject(this.listStem) ;
		oos.close();
	}
	@SuppressWarnings("unchecked")
	public void deserialize() throws FileNotFoundException, IOException, ClassNotFoundException{
		String d  = readFile(name+"_SerialiseDocs.txt");
		strToDocsHm(d);
		String s  = readFile(name+"_SerialiseStems.txt");
		strToStemsHm(s);
		String df = readFile(name+"_SerialiseDocFrom.txt");
		strToDocFromHm(df);
		String prec = readFile(name+"_SerialisePrec.txt");
		strToPredecesseurs(prec);
		String suc = readFile(name+"_SerialiseSucc.txt");
		strToSucesseurs(suc);
		File fichier =  new File("listStem.ser") ;
		ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(fichier)) ;
		this.listStem = (Set<String>) ois.readObject() ;
		ois.close();
	}
	private void setPredecesseurs(ArrayList<String> links, String idDoc){
		for (String s : links) {			
			if(predecesseurs.containsKey(s)){
				predecesseurs.put(s, predecesseurs.get(s)+";"+idDoc);
			}else{
				predecesseurs.put(s, idDoc);
			}
		}
	}
	public HashMap<String,String> getPredecesseurs(){
		return predecesseurs;
	}
	public HashMap<String,String> getSuccesseurs(){
		return successeurs;
	}
	
	public Set<String> getListStem(){
		return this.listStem;
	}
	
	public ArrayList<Integer> getListDocs(){
		return new ArrayList<Integer>(this.docs.keySet());
	}
}
