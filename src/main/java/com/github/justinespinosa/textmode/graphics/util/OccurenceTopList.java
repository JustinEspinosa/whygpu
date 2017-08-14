package com.github.justinespinosa.textmode.graphics.util;

import java.util.Comparator;
import java.util.HashMap;

import com.github.justinespinosa.textmode.curses.ui.util.SortedList;

public class OccurenceTopList<T> extends SortedList<OccurenceCounter<T>> {

	private static final long serialVersionUID = -7966173827977380714L;

	private static class OccurenceComparator<T> implements Comparator<OccurenceCounter<T>>{
		public int compare(OccurenceCounter<T> o1, OccurenceCounter<T> o2) {
			return (new Integer(o2.count())).compareTo(o1.count());
		}
	}
	
	private HashMap<T, OccurenceCounter<T>> objects = new HashMap<T, OccurenceCounter<T>>();
	
	public OccurenceTopList() {
		super(new OccurenceComparator<T>());
	}

	public synchronized void plusOne(T e){
		if(!containsObject(e))
			addObject(e);
		getCounter(e).oneMore();
	}
	protected final OccurenceCounter<T> getCounter(T e){
		return objects.get(e);
	}
	protected final boolean containsObject(T e){
		return objects.containsKey(e);
	}
	
	public T getFirst(){
		if(size()>0)
			return getObject(0);
		return null;
	}
	
	public T getSecond(){
		if(size()>1)
			return getObject(1);
		return null;
	}
	
	public int getFirstCount(){
		if(size()>0)
			return getCount(0);
		return 0;
	}
	
	public int getSecondCount(){
		if(size()>1)
			return getCount(1);
		return 0;
	}
	
	public synchronized void addObject(T e){
		objects.put(e,new OccurenceCounter<T>(e));
		super.add(objects.get(e));
	}
	
	public T getObject(int index){
		return get(index).object();
	}
	
	public int getCount(int index){
		return get(index).count();
	}
	
}
