package com.github.justinespinosa.textmode.curses.term.termcap;


import com.github.justinespinosa.textmode.xfer.util.Arrays;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TermCap {

	private final TreeMap<String,TermType> types = new TreeMap<String, TermType>();;

    public TermCap() throws IOException {
        this(TermCap.class.getClassLoader().getResourceAsStream("termcap.src"));
    }

    public TermCap(String filename) throws IOException {
		this(new FileInputStream(filename));
	}

	public TermCap(InputStream stream) throws IOException {
        this(loadToByteArray(stream));
	}

    public TermCap(byte[] data) throws IOException {
        loadData(data);
    }

    private static byte[] loadToByteArray(InputStream is) throws IOException{
	    byte[] data = new byte[0];
	    byte[] buffer = new byte[4096];
	    int l;
	    while( (l = is.read(buffer)) > -1){
	        data = Arrays.copyOf(data, data.length + l);
	        System.arraycopy(buffer, 0, data, data.length - l , l);
        }
        return data;
    }

	private void loadData(byte[] data) throws IOException {
	    InputStream is = new ByteArrayInputStream(data);
		TermCapFileReader r;

		r   = new TermCapFileReader(new InputStreamReader(is));
		while(r.ready())
			preprocessLine(r.readLine());
		
		is.close();
		is = new ByteArrayInputStream(data);
		
		r = new TermCapFileReader(new InputStreamReader(is));
		while(r.ready())
			parseLine(r.readLine());

		is.close();
		
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
