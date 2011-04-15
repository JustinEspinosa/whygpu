package fun.useless.curses.ui.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class SortedVector<T> extends Vector<T> {

	private static final long serialVersionUID = -948459713994507899L;
	private Comparator<T> comparator;
	
	public SortedVector(Comparator<T> comp) {
		comparator = comp;
	}

	public SortedVector(Comparator<T> comp,int initialCapacity) {
		super(initialCapacity);
		comparator = comp;
	}

	public SortedVector(Comparator<T> comp,int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
		comparator = comp;
	}
	
	@Override
	public synchronized boolean add(T e) {
		int index = Collections.binarySearch(this, e, comparator);
		if(index>-1)
			return false;
		index = (-1*index)-1;
		insertElementAt(e, index);
		return true;
	}
	

}
