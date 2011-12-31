package textmode.graphics.util;

public class OccurenceCounter<T> {
	private T object;
	private int count = 0;
	
	public OccurenceCounter(T o){
		object = o;
		count = 1;
	}
	
	public void oneMore(){
		++count;
	}
	
	public int count(){
		return count;
	}
	
	public T object() {
		return object;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof OccurenceCounter<?>)
			return object.equals(((OccurenceCounter<?>) obj).object);
		
		return object.equals(obj);
	}


}
