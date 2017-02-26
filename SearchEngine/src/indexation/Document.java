package indexation;

import java.util.ArrayList;
import java.util.HashMap;
public class Document {
	private String id;
	private String text="";
	private HashMap<String,String> other;
	private String[] links;
 	public Document(String id,String text,HashMap<String,String> other){
		this.id=id;
		this.text	= text;
		this.other	= other;
		String link = other.get("links");
		if(link==null || link.length()==0){
			links = new String[0];
		}else{
			this.links	= link.split(";");
		}
	}
	public Document(String id,String text){
		this(id,text,new HashMap<String,String>());
	}
	public Document(String id){
		this(id,"");
	}
	public String getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public HashMap<String, String> getOther() {
		return other;
	}
	public String get(String key){
		return other.get(key);
	}
	public HashMap<String, Integer> getDocStem(){
		Stemmer stemmer = new Stemmer();
		return stemmer.getTextRepresentation(text);
	}
	public void set(String key,String val){
		other.put(key, val);
	}
	public ArrayList<String> getLinks(){
		ArrayList<String> l = new ArrayList<String>();
		for(int i=0; i<links.length; i++){
			if(links[i]!=null && links[i].length()>0 && !l.contains(links[i]))
				l.add(links[i]);
		}
		return l;
	}
	
	public String getLinksForSucc(){
		ArrayList<String> L = new ArrayList<String>();
		for(int i=0; i<links.length; i++){
			if(links[i]!=null && links[i].length()>0 && !L.contains(links[i]))
				L.add(links[i]);
		}
		
		String l = "";
		/*for(int i=0; i<links.length; i++){
			if(links[i]!=null && links[i].length()>0 && !l.contains(links[i]))
				l+= links[i]+";";
		}*/
		for(String s : L){
			l+=s+";";
		}
		
		if(l.length() > 1)
			l = l.substring(0,l.length() - 1);
		return l;
	}
}
