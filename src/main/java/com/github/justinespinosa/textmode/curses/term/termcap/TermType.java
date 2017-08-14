package com.github.justinespinosa.textmode.curses.term.termcap;

import java.util.TreeMap;
import java.util.Vector;

public class TermType {

	private TreeMap<String,Object> parameters = new TreeMap<String,Object>();
	private Vector<TermType> tc = new Vector<TermType>();
	private String name="";
	
	public TermType(){
	}
	
	public void setName(String n){
		//System.out.println("I am "+n);
		name=n;
	}
	
	public void setTC(TermType tt){
		//System.out.println(this+" depends on "+tt);
		tc.add(tt);
	}
	
	public void set(String k,Object v){
		parameters.put(k, v);
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	private Object getFromTC(String k){
		for (TermType tt : tc) {
			Object o =tt.get(k);
			if(o!=null) 
				return o;
		}
		return null;
	}
	private Object get(String k)
	{
		if(parameters.containsKey(k))
			return parameters.get(k);
		
		if(tc!=null)
			return getFromTC(k);
		
		return null;
	}
	public boolean tgetflag(String key){ return getFlag(key); }
	public boolean getFlag(String key){
		Object o = get(key);
		
		if(o instanceof Boolean)
			return ((Boolean)o).booleanValue();
		
		return false;
	}
	public int tgetnum(String key){ return getNum(key); }
	public int getNum(String key){
		Object o = get(key);
		
		if(o instanceof Integer)
			return ((Integer)o).intValue();
		
		return -1;
	}
	public String tgetstr(String key){ return getStr(key); }
	public String getStr(String key){
		Object o = get(key);
		
		if(o instanceof String)
			return (String)o;
		
		return null;		
	}
}
