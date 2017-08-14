package com.github.justinespinosa.textmode.data.xml;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterator implements Iterator<Node>{
	private NodeList list;
	private Node node;
	private int index = -1;
	
	public NodeIterator(Node parent){
		node = parent;
		list = node.getChildNodes();
	}

	public boolean hasNext() {
		return (index+1)<list.getLength();
	}

	public Node next() {
		return list.item(++index);
	}

	public void remove() {
		node.removeChild(list.item(index));
		--index;
		list = node.getChildNodes();
	}
}