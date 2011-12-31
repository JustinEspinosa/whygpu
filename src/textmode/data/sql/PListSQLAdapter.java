package textmode.data.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

import textmode.data.PropertyList;

public final class PListSQLAdapter {
	
	/*
	 * Dirty String work.
	 * 
	 * 
	 */
	
	private static char Escaper = '\\';
	private static char Quoter = '\'';
	
	private static enum ValueType{ STRING,PLIST,NUMERIC; 
		private static <T extends PropertyList>ValueType guess(Class<?> type){
			if(PropertyList.class.isAssignableFrom(type))
				return PLIST;
			if(String.class.isAssignableFrom(type))
				return STRING;
			return NUMERIC;
		}
		private static <T extends PropertyList>ValueType guess(T list, String name){
			return guess(list.typeOf(name));
		}
	}
	private static enum CopyDepth{ FIELD,VALUE,BOTH; }

	
	private static <T extends PropertyList> void putFieldName(T list, String name, StringBuilder builder){
		builder.append(name);
	}
	
	/* wc = wrap characters */
	private static CharSequence wc(char ... chrs){
		return new String(chrs);
	}
	
	private static String escapeQuotes(String str, char quote){
		return str.replace(wc(quote),wc(Escaper,quote));
	}
	
	private static String escapeQuotes(String str){
		return escapeQuotes(str,Quoter);
	}
	
	private static void putTextualContent(String value, StringBuilder builder){
		builder.append(Quoter);
		builder.append(escapeQuotes(value));
		builder.append(Quoter);
	}
	
	private static void putNumericContent(Object value, StringBuilder builder){
		builder.append(value.toString());
	}
	
	private static Map<String,Object> propertyIdToMap(PropertyList list){
		TreeMap<String,Object> map = new TreeMap<String, Object>();
		String[] names = list.persistantKeys();
		for(String name : names)
			map.put(name, list.get(name, list.typeOf(name)));

		return map;
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,Object> unserialzePropertyIdMap(String serialData){
		Object o = null;
		try {
			ObjectInputStream unSerializer = new ObjectInputStream(new ByteArrayInputStream(serialData.getBytes()));
			o = unSerializer.readObject();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();//just in case...
		}
		
		if(o instanceof Map<?,?>)
			return (Map<String,Object>)o;
		
		return new TreeMap<String,Object>();
	}
	
	private static void fillPropertyListWithKeysFrom(PropertyList list, Map<String,Object> data){
		for(String key : data.keySet() ) 
			list.set(key, data.get(key));		
	}
	
	private static void putPropertyListKeyContent(PropertyList value, StringBuilder builder){
		ByteArrayOutputStream serialData = new ByteArrayOutputStream();
		ObjectOutputStream serializer;
		
		try {
			
			serializer = new ObjectOutputStream(serialData);
			serializer.writeObject(propertyIdToMap(value));
			
		} catch (IOException e) {}
		
		putTextualContent(serialData.toString(), builder);
	}
	
	private static <T extends PropertyList> void putDecoratedValue(T list, String name, StringBuilder builder){
		
		switch(ValueType.guess(list,name)){
		case STRING:
			putTextualContent(list.get(name, String.class), builder); break;
		case PLIST:
			putPropertyListKeyContent(list.get(name, PropertyList.class), builder); break;
		case NUMERIC:
			putNumericContent(list.get(name, Object.class), builder); break;
		}

	}
	
	private static <T extends PropertyList> void putItem(T list, String name, StringBuilder builder, CopyDepth depth){
		switch(depth){
		case VALUE:
			putDecoratedValue(list, name, builder); break;
		case FIELD:
			putFieldName(list, name, builder); break;
		case BOTH:
			putFieldName(list, name, builder);
			builder.append('=');
			putDecoratedValue(list, name, builder);
		break;
		}
	}
	
	private static <T extends PropertyList> void addItem(T list, String name, StringBuilder builder, CopyDepth depth, String separator){
		builder.append(separator);
		putItem(list, name, builder, depth);
	}

	private static <T extends PropertyList> void putList(T list, String[] names, StringBuilder builder, CopyDepth depth, String separator){
		putItem(list, names[0], builder, depth);
		for(int i=1;i<names.length;++i)
			addItem(list, names[i], builder, depth, separator);
	}
	
	private static <T extends PropertyList> void putList(T list, String[] names, StringBuilder builder, CopyDepth depth){
		putList(list, names, builder, depth, ",");
	}
	
	
	
	
	
	
	/**
	 * Generates an insert query 
	 * @param list
	 * @return
	 */
	
	public static <T extends PropertyList> String getInsert(T list){
		StringBuilder builder = new StringBuilder();
		String[] properties = list.persistantProperties();
		
		builder.append("Insert Into ");
		builder.append(list.getClass().getSimpleName());
		builder.append('(');
		putList(list, properties, builder, CopyDepth.FIELD);
		builder.append(") Values (");
		putList(list, properties, builder, CopyDepth.VALUE);
		builder.append(")");
		
		return builder.toString();
	}
	
	/**
	 * Generates an update query (never updates the primary keys)
	 * @param list
	 * @return
	 */
	
	public static <T extends PropertyList> String getUpdate(T list){
		StringBuilder builder = new StringBuilder();
		
		builder.append("Update ");
		builder.append(list.getClass().getSimpleName());
		builder.append(" Set ");
		putList(list,list.persistantValues(), builder, CopyDepth.BOTH);
		builder.append(" Where ");
		putList(list,list.persistantKeys(), builder, CopyDepth.BOTH, " and ");
		
		return builder.toString();
		
	}
	
	/**
	 * Generates a select query using the annotated primary keys of the property list
	 * @param list
	 * @return
	 */
	public static <T extends PropertyList> String getSelect(T list){
		StringBuilder builder = new StringBuilder();
		
		builder.append("Select ");
		putList(list,list.persistantProperties(),  builder, CopyDepth.FIELD);
		builder.append(" From ");
		builder.append(list.getClass().getSimpleName());
		builder.append(" Where ");
		putList(list,list.persistantKeys(), builder, CopyDepth.BOTH, " and ");
		
		return builder.toString();
		
	}
	
	private static <T extends PropertyList> PropertyList loadForeignPropertyList(T list, String name, String value) 
			throws InstantiationException, IllegalAccessException{
		
		PropertyList subList = (PropertyList) list.typeOf(name).newInstance();
		fillPropertyListWithKeysFrom(subList, unserialzePropertyIdMap(value) );
				
		return subList;
	}
	
	public static <T extends PropertyList> Object convertItem(T list, String name, Object value)
			throws InstantiationException, IllegalAccessException{
		switch(ValueType.guess(list, name)){
		case PLIST:
			return loadForeignPropertyList(list, name, value.toString()); 
		case STRING:
		case NUMERIC:
		default:
			return value;
		}
	}
}
