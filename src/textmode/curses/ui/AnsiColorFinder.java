package textmode.curses.ui;

import java.util.Comparator;

import java.util.Arrays;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AnsiColorFinder<T extends Color>{

	private static class IndexOnlyColor implements Color{

		private int index;
		private IndexOnlyColor(int i){
			index = i;
		}
		
		public int index() {
			return index;
		}

		public RGB rgb() { throw new NotImplementedException(); }	
		public ColorDepth depth() { throw new NotImplementedException(); }	
	}
	
	private static class ColorComparator implements Comparator<Color>{
		public int compare(Color o1, Color o2) {
			return (new Integer(o1.index()).compareTo(o2.index()));
		}
	}
	
	private ColorComparator comparator = new ColorComparator();
	private T[] values;
	
	@SuppressWarnings("unchecked")
	public AnsiColorFinder(Class<? extends Enum<?>> type){
		values = (T[]) type.getEnumConstants();
		Arrays.sort(values, comparator);
	}
	
	public T find(int index){
		int i = Arrays.binarySearch(values, new IndexOnlyColor(index), comparator );
		if(i<0) throw new NullPointerException();
		return values[i];
	}
}
