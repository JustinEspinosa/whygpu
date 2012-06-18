package textmode.curses.ui;

import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;


/**
 * Buffers and simplifies rectangles
 * 
 * Currently do not simplify that much. Needs a good algorithm.
 * 
 * @author justin
 *
 */
public class RectangleBuffer {

	private static class LineList extends TreeMap<Integer,Vector<Segment>>{

		private static final long serialVersionUID = 196675993166082098L;

		public boolean put(Segment value) {
			Integer key = value.getLine();
			if(get(key)==null)
				super.put(key, new Vector<Segment>());
			
			return get(key).add(value);
		}
		
		@Override
		public Vector<Segment> get(Object key) {
			Vector<Segment> s = super.get(key);
			if(s==null){
				s = new Vector<Segment>();
				super.put((Integer)key, s);
			}
			return s;
		}
	}
	
	private LineList area = new LineList();
	
	private void reduce(Rectangle r){
		
		Segment[] slices = Segment.slice(r);
		
		for(Segment seg : slices){
			Enumeration<Segment> eS = area.get(seg.getLine()).elements();
			boolean absorbed = false;
			while(eS.hasMoreElements()){
				Segment cS = eS.nextElement();
				if(absorbed = cS.absorb(seg)) 
					break;
			}
			if(!absorbed)
				area.put(seg);
		}
		
	}
	

	
	public synchronized void addToArea(Rectangle r){
		reduce(r);
	}
	
	public synchronized Rectangle[] getArea() {
		
		int size=0;
		for(Vector<Segment> l : area.values() )
			size += l.size();
		
		Rectangle[] arrCpy = new Rectangle[size];
		int i = 0;
		for(Vector<Segment> l : area.values() ){
			for(Segment s : l)
				arrCpy[i++] = s.toRectangle();

			l.clear();
		}

		return arrCpy;
	}
	
}
