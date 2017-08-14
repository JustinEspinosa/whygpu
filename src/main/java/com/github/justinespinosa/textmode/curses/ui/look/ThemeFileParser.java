package com.github.justinespinosa.textmode.curses.ui.look;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import com.github.justinespinosa.textmode.curses.ui.Color;
import com.github.justinespinosa.textmode.curses.ui.ColorPair;
import com.github.justinespinosa.textmode.curses.ui.components.Component;

public class ThemeFileParser {

	private static class NamedColorTheme extends ColorTheme{
		private ColorManager manager;
		private String name;
		private NamedColorTheme(String nm,ColorManager man){
			name = nm;
			manager = man;
		}
		public ColorManager getColorManager() {
			return manager;
		}
		public String name(){
			return name;
		}
	}
	
	private static class NodeListIterator implements Iterator<Node>{
		private NodeList list;
		private int index = -1;
		
		private NodeListIterator(NodeList lst){
			list = lst;
		}

		public boolean hasNext() {
			return (index+1)<list.getLength();
		}

		public Node next() {
			return list.item(++index);
		}

		public void remove() {
			throw new NotImplementedException();
		}
	}
	
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	
	public ThemeFileParser() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
	}
	
	private String attribute(Node n,String name){
		return n.getAttributes().getNamedItem(name).getNodeValue();
	}
	private String contentText(Node n){
		return n.getFirstChild().getNodeValue();
	}
	private boolean name(Node n,String name){
		return name.equals(n.getNodeName());
	}
	
	private void addColor(Node n,ColorManager manager,Class<? extends Component> compClass, String type) throws DOMException, ClassNotFoundException, InstantiationException{
		
		Iterator<Node> nodes = new NodeListIterator(n.getChildNodes());
		Color fg=null, bg=null;
		while(nodes.hasNext()){
			Node that = nodes.next();
			if(name(that,"foreGround"))
				fg = ColorTheme.extractColor(contentText(that));
			if(name(that,"backGround"))
				bg = ColorTheme.extractColor(contentText(that));
		}
		
		if(bg!=null && fg!=null)
			manager.setCustom(compClass,type, new ColorPair(fg,bg));
	}
	
	private ColorTheme parseTheme(Node n) throws DOMException, ClassNotFoundException, InstantiationException{
		ColorTheme theme = new NamedColorTheme(attribute(n,"name"),ColorManager.createInstance());
		
		Iterator<Node> nodes = new NodeListIterator(n.getChildNodes());
		while(nodes.hasNext()){
			Node that = nodes.next();
			if(name(that,"component")){
				Class<? extends Component> compClass = ColorTheme.extractClass(attribute(that,"class"));
				parseComponent(that, theme, compClass);
			}
		}
		
		return theme;
	}
	
	private void parseComponent(Node n,ColorTheme theme,Class<? extends Component> compClass) throws DOMException, ClassNotFoundException, InstantiationException {
		Iterator<Node> nodes = new NodeListIterator(n.getChildNodes());
		while(nodes.hasNext()){
			Node that = nodes.next();
			if(name(that,"color")){
				String type = "";
				if(attribute(that,"type").equals("standard"))
					type = ColorManager.STDCOL;
				if(attribute(that,"type").equals("alternate"))
					type = ColorManager.ALTCOL;
				if(attribute(that,"type").equals("custom"))
					type = attribute(that,"label");
				
				addColor(that,theme.getColorManager(),compClass,type);
			}
		}

	}
	
	public ColorTheme[] parse(File f) throws SAXException, IOException, InvalidFileTypeException{
		Document document = builder.parse(f);
		Node rootNode = document.getFirstChild();
		if(!name(rootNode,"themeFile"))
			throw new InvalidFileTypeException();
		
		List<ColorTheme> themes = new ArrayList<ColorTheme>();
		
		Iterator<Node> nodes = new NodeListIterator(rootNode.getChildNodes());
		while(nodes.hasNext()){
			Node theme = nodes.next();
			if(name(theme,"theme"))
				try{
					themes.add(parseTheme(theme));
				}catch(Exception e){
					System.err.println("Exception in parsing: "+e.getClass().getName()+": "+e.getMessage());
					e.printStackTrace();
				}
		}
		
		return themes.toArray(new ColorTheme[themes.size()]);
	}
	
}
