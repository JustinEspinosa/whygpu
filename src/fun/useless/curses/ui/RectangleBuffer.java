package fun.useless.curses.ui;

import java.util.Enumeration;
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

	private Vector<Rectangle> area = new Vector<Rectangle>();
	
	private void reduce(Rectangle r){
		Enumeration<Rectangle> eR = area.elements();
		
		while(eR.hasMoreElements()){
			Rectangle cR = eR.nextElement();
			if(cR.includes(r)) return;
		    if(r.includes(cR)){
		    	area.remove(cR);
		    	area.add(r);
		    	return;
		    }
		}
		area.add(r);
	}
	
	public synchronized void addToArea(Rectangle r){
		reduce(r);
	}
	
	public synchronized Rectangle[] getArea(){
		Rectangle[] arrCpy = new Rectangle[area.size()];
		area.toArray(arrCpy);
		area.clear();
		return arrCpy;
	}
	
}
