package com.github.justinespinosa.textmode.data.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.justinespinosa.textmode.data.PropertyList;

public class XMLPListNodeReader {

	private Document document;
	private Node     rootNode;
	
	public XMLPListNodeReader(Document doc){
		document = doc;
		rootNode = document.getFirstChild();
	}
	
	private String attribute(Node n,String name){
		return n.getAttributes().getNamedItem(name).getNodeValue();
	}
	private String contentText(Node n){
		try{
			
			if(n.hasChildNodes())
				return n.getFirstChild().getNodeValue();
			
			return "";
			
		}catch(NullPointerException e){
			return "";
		}
	}
	private boolean name(Node n,String name){
		return name.equals(n.getNodeName());
	}
	
	@SuppressWarnings("unchecked")
	private PropertyList readPList(Node n) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException{
		Iterator<Node> i = new NodeIterator(n);
		
		Class<?> nClass = readType(n);
		if(!PropertyList.class.isAssignableFrom(nClass))
			throw new ClassCastException(nClass.getName()+" is not a PropertyList");
		
		Class<? extends PropertyList> pListClass = (Class<? extends PropertyList>)nClass;
		PropertyList pList = pListClass.newInstance();
		
		while(i.hasNext())
			setProperty(pList,i.next());
		
		return pList;
	}
	
	private void setProperty(PropertyList plist, Node n) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException{
		
		Object o = readNode(n);
		if(o!=null){
			try{
				String name = attribute(n,"name");
				plist.set(name, o);
				
				//Support change of format, ignore elements not available in
				//class (will be erased if the file is saved).
			}catch(NoSuchElementException e){
			}
		}
	}
	
	private Class<?> readType(Node n) throws ClassNotFoundException{
		String pListClName = attribute(n,"class");
		Class<?> nClass = Class.forName(pListClName);
		return nClass;
	}
	
	private Object readValue(Node n) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> nClass = readType(n);
		String content = contentText(n);
		
		Constructor<?> constr = nClass.getConstructor(String.class);
		if(constr==null)
			throw new InstantiationException("Type is not instantiable from String");
		
		return constr.newInstance(content);
	}
	
	private Object readNode(Node n) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException{
		if(name(n,"PropertyList"))
			return readPList(n);
		if(name(n,"Value"))
			return readValue(n);
		
		return null;
	}

	public SortedMap<PropertyList,Node> readDocument(){
		SortedMap<PropertyList,Node> ret = new TreeMap<PropertyList,Node>();
		Iterator<Node> i = new NodeIterator(rootNode);
		while(i.hasNext()){
			Node n = i.next();
			try {
				Object o = readNode(n);
				if(o instanceof PropertyList){
					ret.put((PropertyList) o, n);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}
		
		return ret;
	}

}
