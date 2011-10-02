package textmode.curses.ui.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

public class SortedList<T> extends ArrayList<T> {

	private static final long serialVersionUID = -948459713994507899L;
	private Comparator<T> comparator;
	
	public SortedList(Comparator<T> comp) {
		comparator = comp;
	}

	public SortedList(Comparator<T> comp,int initialCapacity) {
		super(initialCapacity);
		comparator = comp;
	}
	
	protected Comparator<T> comparator(){
		return comparator;
	}
	
	/* type erasure is pissing me off */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		int index = Collections.binarySearch(this,(T)o, comparator);
		return (index>-1);		
	}
	
	@Override
	public synchronized boolean add(T e) {
		int index = Collections.binarySearch(this, e, comparator);
		if(index>-1)
			return false;
		index = (-1*index)-1;
		
		add(index,e);
		
		return true;
	}
	

}
