package textmode.curses.ui.util;

import java.util.TreeMap;
import java.util.LinkedList;


import textmode.curses.ui.Position;
import textmode.curses.ui.components.Component;

public class ChildrenList<T>{
	private TreeMap<Component,Position> locations = new TreeMap<Component, Position>();
	private LinkedList<Component> components = new LinkedList<Component>();
	
	public int count(){
		return components.size();
	}
}