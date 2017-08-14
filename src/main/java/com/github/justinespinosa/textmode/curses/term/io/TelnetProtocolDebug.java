package com.github.justinespinosa.textmode.curses.term.io;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

class TelnetProtocolDebug {
	
	static Map<Byte,String> names = new HashMap<Byte, String>();
	
	static{
		Field[] fields = TelnetIO.class.getDeclaredFields();
		for(Field field: fields){
			int mods = field.getModifiers();
			if(Modifier.isFinal(mods) && Modifier.isStatic(mods) &&
				field.getType() == byte.class ){
					try {
						names.put(field.getByte(null),field.getName());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	static String getName(byte b){
		if(names.containsKey(b)){
			return names.get(b);
		}
		System.out.println("Unknown protocol value: "+b);
		return "UNKNOWN";
	}
	
}
