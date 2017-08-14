package com.github.justinespinosa.textmode.data.xml;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.github.justinespinosa.textmode.data.PropertyList;

public class XMLPListNodeFactory {
	private Document document;
	public XMLPListNodeFactory(Document doc){
		 document = doc;
	}
	
	public void setup(){
		Element root = document.createElement("PropertyFile");
		document.appendChild(root);
	}
	
	public void cleanUp(){
		Node root = document.getFirstChild();
		Iterator<Node> i = new NodeIterator(root);
		while(i.hasNext()){
			Node n = i.next();
			if(n.getNodeType()==Node.TEXT_NODE)
				i.remove();
		}
	}
	
	public <T> Node createNode(String fieldName,T object){
		String name;
		
		if(PropertyList.class.isAssignableFrom(object.getClass()))
			name = "PropertyList";
		else
			name = "Value";

		Element node = document.createElement(name);
		node.setAttribute("class", object.getClass().getName());
		
		if(fieldName!=null)
			node.setAttribute("name", fieldName);
		
		if(PropertyList.class.isAssignableFrom(object.getClass())){
			
			PropertyList plist = (PropertyList)object;
			String[] properties = plist.persistantProperties();
			for(String property:properties)
				node.appendChild(createNode(property,plist.get(property, plist.typeOf(property))));

		}else{
			Text tnode = document.createTextNode(object.toString());
			node.appendChild(tnode);
		}
		
		return node;
	}
	
}
