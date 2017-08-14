package com.github.justinespinosa.textmode.curses.ui.util;

import java.util.Collections;
import java.util.Comparator;

public class WeightSortedList<T extends WeightObject> extends SortedList<WeightObject> {

	private static final long serialVersionUID = -498592464209807130L;

	private static class WOComp implements Comparator<WeightObject>{

		public int compare(WeightObject o1, WeightObject o2) {
			return (int) (o1.weight() - o2.weight());
		}
		
	}
	
	public WeightSortedList() {
		super(new WOComp());
	}
	
	public WeightSortedList(int initialCapacity) {
		super(new WOComp(),initialCapacity);
	}


	@SuppressWarnings("unchecked")
	public T findNearest(WeightObject obj){
		int index = Collections.binarySearch(this, obj, comparator());
		
		if(index<0){
			index = (-1*index)-1;
			if(index>0){
				T wogr = (T)get(index);
				T wole = (T)get(index-1);
				
				if(wogr.weight()-obj.weight()>obj.weight()-wole.weight())
					return wogr;
				else
					return wole;
			}
		}
		
		return (T)get(index);
	}
}
