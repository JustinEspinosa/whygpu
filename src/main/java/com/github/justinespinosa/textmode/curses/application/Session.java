package com.github.justinespinosa.textmode.curses.application;

import java.util.HashMap;
import java.util.Map;

public class Session{

	private Map<String,Object> properties = new HashMap<String, Object>();
	
	public Session(){
	}
	
	public void set(String key,Object o){
		put(key,o);
	}
	
	public void modify(String key,Object o){
		put(key,o);
	}
	
	public void put(String key,Object o){
		properties.put(key, o);
	}
	
	public boolean contains(String key){
		return properties.containsKey(key);
	}
	
	public Object get(String key){
		return properties.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAs(String key){		
		return (T) get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAsChecked(String key,Class<T> cls){
		Object o = get(key);
		
		if(o==null) return null;
		if(cls==null) return getAs(key);
		
		if(cls.isInstance(o))
			return (T) (o);
		
		return null;
	}
	
	public <T> T getSingleton(Class<T> cls){
		return getAsChecked(cls.getName(),cls);
	}

}
