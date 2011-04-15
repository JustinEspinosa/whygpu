package fun.useless.curses.term.termcap;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class TermCap {

	private TreeMap<String,TermType> types;
	
	public TermCap(String filename) throws IOException {
		types = new TreeMap<String, TermType>();
		
		loadFile(filename);
	}
	
	private void loadFile(String filename) throws IOException {
		FileInputStream fis;
		TermCapFileReader r;
		
		fis = new FileInputStream(filename);
		r   = new TermCapFileReader(new InputStreamReader(fis));
		while(r.ready())
			preprocessLine(r.readLine());
		
		fis.close();
		
		fis = new FileInputStream(filename);
		r = new TermCapFileReader(new InputStreamReader(fis));
		while(r.ready())
			parseLine(r.readLine());
		
		fis.close();
		
	}
	
	private void preprocessLine(String line){
		if(line==null) return;
		
		TermType type = new TermType();
		
		String[] parts = line.split("(?<!\\\\):");
		String[] names = parts[0].split("\\|");
		
		if(names.length>0)
			type.setName(names[0]);
		
		for(int i=0;i<names.length;i++)
			types.put(names[i],type);		
	}
	private void parseLine(String line){
		if(line==null) return;
		
		String[] parts = line.split("(?<!\\\\):");
		String[] names = parts[0].split("\\|");
		
		TermType type = types.get(names[0]);
		
		for(int i=1;i<parts.length;i++)
			if(parts[i].length()>0)
				parseAttribute(parts[i],type);
	}
	
	private void parseAttribute(String attr,TermType type){
		int i;
		
		if( (i=attr.indexOf('='))>0 )
			parseString(attr.substring(0,i),attr.substring(i+1),type);
		else
			if( (i=attr.indexOf('#'))>0 )
				parseNum(attr.substring(0,i),attr.substring(i+1),type);
			else
				parseFlag(attr, type);
			
	}
	private void parseFlag(String key,TermType type){
		boolean val = true;
		if(key.charAt(key.length()-1)=='@')
			val =false;
		type.set(key, new Boolean(val));
	}
	private void parseNum(String key,String val,TermType type){
		try{
			type.set(key, Integer.parseInt(val));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}	
	private void parseString(String key,String val,TermType type){
		if(!key.equals("tc"))
			type.set(key, val);
		else
			type.setTC(types.get(val));
	}
	
	public TermType tgetent(String name){
		return getTermType(name);
	}
	public TermType getTermType(String name){
		return types.get(name);
	}
}
