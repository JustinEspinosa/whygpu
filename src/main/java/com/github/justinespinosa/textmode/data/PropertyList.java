package com.github.justinespinosa.textmode.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class PropertyList implements Comparable<PropertyList>{
	
	/***********************************
	 * Bird.
	 */
	
	public PropertyList(){}
	
	private Field getProperty(String name) throws NoSuchElementException{
		try{
			
			Field field = getClass().getField(name);
			Property property = field.getAnnotation(Property.class);
			
			if(property==null )
				throw new NoSuchElementException();
			
			return field;
			
		}catch(NoSuchFieldException e){
			throw new NoSuchElementException();
		}catch(SecurityException e){
			throw new NoSuchElementException();
		}
	}

	private <T> void setProperty(String name, T value, Class<T> type) throws NoSuchElementException{
		try{
			Field field = getProperty(name);
			
			if(!type.isAssignableFrom(field.getClass()))
				throw new NoSuchElementException();
			
			 field.set(this, value);
					
		}catch(SecurityException e){
			throw new NoSuchElementException();
		} catch (IllegalArgumentException e) {
			throw new NoSuchElementException();
		} catch (IllegalAccessException e) {
			throw new NoSuchElementException();
		}		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getProperty(String name, Class<T> type) throws NoSuchElementException{
		try{
			Field field = getProperty(name);
			
			if(!type.isAssignableFrom(field.getType()))
				throw new NoSuchElementException();
			
			return (T)field.get(this);
					
		}catch(SecurityException e){
			throw new NoSuchElementException();
		} catch (IllegalArgumentException e) {
			throw new NoSuchElementException();
		} catch (IllegalAccessException e) {
			throw new NoSuchElementException();
		}		
	}
	
	private static enum SearchType{ALL,PERSISTANTS,KEYS,KEYS_PERSISTANT,PERSISTANTS_NOKEYS};

	public String[] properties(){
		return properties(SearchType.ALL);
	}
	public String[] persistantValues(){
		return properties(SearchType.PERSISTANTS_NOKEYS);
	}
	public String[] persistantKeys(){
		return properties(SearchType.KEYS_PERSISTANT);
	}
	public String[] persistantProperties(){
		return properties(SearchType.PERSISTANTS);
	}
	public String[] keyProperties(){
		return properties(SearchType.KEYS);
	}
	
	private String[] properties(SearchType search){
		List<String> result = new ArrayList<String>();
		Field[] fields = getClass().getFields();
		for(Field field : fields){
			try{
				field = getProperty(field.getName());
				
				switch(search){
				case PERSISTANTS_NOKEYS:
					if(field.getAnnotation(Property.class).persistant() && !field.getAnnotation(Property.class).key())
						result.add(field.getName());
					break;
				case KEYS_PERSISTANT:
					if(field.getAnnotation(Property.class).persistant() && field.getAnnotation(Property.class).key())
						result.add(field.getName());
					break;
				case PERSISTANTS:
					if(field.getAnnotation(Property.class).persistant())
						result.add(field.getName());
					break;
				case KEYS:
					if(field.getAnnotation(Property.class).key())
						result.add(field.getName());
					break;
				case ALL:
				default:
					result.add(field.getName());
				}
			}catch(NoSuchElementException e){ }
		}
		return result.toArray(new String[result.size()]);
	}
	
	
	public boolean isKey(String name){
		Field field = getProperty(name);
		return field.getAnnotation(Property.class).key();
	}
	
	public Class<?> typeOf(String name){
		Field field = getProperty(name);
		return field.getType();
	}
	
	public <T> T get(String name, Class<T> type){
		return getProperty(name,type);
	}
	
	public void set(String name, Object value){
		Field field = getProperty(name);
		try {
			field.set(this, value);
		} catch (IllegalArgumentException e) {
			throw new NoSuchElementException();
		} catch (IllegalAccessException e) {
			throw new NoSuchElementException();
		}
	}
	
	public <T> void set(String name, T value, Class<T> type){
		setProperty(name,value,type);
	}
	
	public int compareTo(PropertyList o) {
		String[] keys = keyProperties();
		String[] fKeys = o.keyProperties();
		
		if(keys.length!=fKeys.length)
			return new Integer(keys.length).compareTo(fKeys.length);
		
		for(String key:keys){
			if(o.isKey(key)){
				try{
					Object fKeyVal = o.get(key, typeOf(key));
					int comp = fKeyVal.toString().compareTo(get(key,typeOf(key)).toString());
					if(comp!=0)
						return comp;
				}catch(NoSuchElementException e){
					return 1;
				}
			}else{
				return 1;
			}
		}
		
		return 0;
	}
	
}
