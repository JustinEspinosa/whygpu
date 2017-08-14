package com.github.justinespinosa.textmode.curses.ui.look;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.github.justinespinosa.textmode.curses.ui.Color;
import com.github.justinespinosa.textmode.curses.ui.components.Component;


public abstract class ColorTheme {
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Color extractColor(String name) throws ClassNotFoundException, InstantiationException{
		int offset;
		String clName, arg;
		name = name.trim();
		
		offset = name.indexOf("(");
		if(offset<0){
			offset = name.lastIndexOf('.');
			arg = name.substring(offset+1).trim();
		}else{
			arg = name.substring(offset+1,name.length()-1).trim();
		}
		clName = name.substring(0,offset).trim();
		
		Class<?> c = Class.forName(clName);
		
		if(c.isEnum()){
			Class<Enum> ce = (Class<Enum>)c;
			return (Color) Enum.valueOf(ce, arg);
		}else{
			String[] args = arg.split(",");
			Constructor<?>[] constrs = c.getConstructors();
			for(Constructor constr: constrs){
				Class[]  params = constr.getParameterTypes();
				if(params.length == args.length){
					List<Object> converted = new ArrayList<Object>();
					for(int n=0;n<params.length;++n){
						Class<?> pType = params[n];
						
						if(String.class.isAssignableFrom(pType))
							converted.add(args[n]);
						if(Integer.class.isAssignableFrom(pType))
							converted.add(Integer.decode(args[n]));	
						if(pType.isPrimitive()){
							if(Integer.TYPE.equals(pType))
								converted.add(Integer.decode(args[n]));
						}
					}
					if(converted.size()==args.length){
						try {
							return (Color) constr.newInstance(converted.toArray());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		throw new InstantiationException();
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Component> extractClass(String className) throws ClassNotFoundException{

		Class<?> c = null;
		
		c = Class.forName(className.trim());
		if(Component.class.isAssignableFrom(c))
			return (Class<? extends Component>)c;
		
		throw new ClassNotFoundException(className+" is not an instance of "+Component.class.getName());

	}
	
	
	public abstract String name();
	public abstract ColorManager getColorManager();
}
