package textmode.curses.ui.components;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.Rectangle;
import textmode.curses.ui.event.EventReceiver;
import textmode.curses.ui.event.UiEvent;



public abstract class Container<T extends Component> extends Component{
	
	
	private Vector<T> childComponents = new Vector<T>();
	private T focused = null;
	
	/**
	 * TODO Not good because it skips the check the prevent to draw off-limits.
	 *      implement a real solution for this problem
	 */
	protected boolean transparent = false;

	public Container(Curses cs,Position p,Dimension d){
		super(cs,p,d);
	}
	

	public void bringToFront(T c){
		bringToFront(c,true);
	}
	//Slow if lots of children
	public synchronized void bringToFront(T c, boolean focus){
		int i = childComponents.indexOf(c);
		if(i >= 0 ){
			childComponents.remove(i);
			intAddChild(c);
			if(focus)
				setFocus(c);
		}
	}
	
	protected final T getFocused(){
		return focused;
	}
	
	public synchronized int getChildCount(){
		return childComponents.size();
	}

	public synchronized T lastChild(){
		return childComponents.lastElement();
	}
	
	public synchronized T firstChild(){
		return childComponents.firstElement();
	}
	
	protected synchronized final int getIndexOf(T comp){
		if(childComponents.contains(comp))
			return childComponents.indexOf(comp);
		else
			return -1;
	}
	
	protected synchronized final T getAtIndex(int index){
		try{
			return childComponents.elementAt(index);
		}catch(ArrayIndexOutOfBoundsException aioobe){
			aioobe.printStackTrace();
			return null;
		}
	}
	
	protected synchronized void intRemoveChildren(){
		Enumeration<T> ec = children();
		while(ec.hasMoreElements())
			ec.nextElement().setEventReceiver(null);
		childComponents.clear();
	}
	
	protected synchronized void intRemoveChild(T c){
		childComponents.remove(c);
		c.setEventReceiver(null);
	}
	
	protected synchronized void intAddChild(T c){
		childComponents.add(c);
		c.setEventReceiver(getEventReceiver());
	}
	
	private void focusNextFocusable(int i){
		
		if(i>=getChildCount()) i=0;
		
		for(int n = 0;n<getChildCount();n++){
			int index = n+i;
			if(index>=getChildCount())
				index-=getChildCount();
			
			if(childComponents.elementAt(index).acceptsFocus()){
				setFocus(childComponents.elementAt(index));
				return;
			}
		}
	}
	
	public synchronized void cycleFocus(){
		if(focused == null){
			focusNextFocusable(0);
			return;
		}
		int idx = childComponents.indexOf(focused);
		if(idx > -1){
			focusNextFocusable(idx+1);
		}
		
	}
	
	@Override
	protected synchronized void setEventReceiver(EventReceiver l){
		Enumeration<T> ec = children();
		while(ec.hasMoreElements())
			ec.nextElement().setEventReceiver(l);
		super.setEventReceiver(l);
	}
	
	@Override
	public synchronized ColorChar getCharAt(Position p){
		
		ColorChar cc   = null;
		ColorChar myCc = super.getCharAt(p);
	
		//don't draw children if i'm not under them
		if(myCc!=null || transparent)
			for( int z = childComponents.size() - 1 ; z >= 0 && cc==null; z--){
				T c = childComponents.elementAt(z);
				
				if(c.isVisible()) 
					cc = c.getCharAt(  p.relativeTo(c.getPosition()) );
			}	
		
		if(cc == null)
			cc = myCc;
			
		return cc;
	}
	protected final void setFocusNoNotify(T c){
		if(c==null) return;
		focused = c;
	}
	protected void setFocus(T c){
		if(c==null) return;
		if(focused!=null) focused.lostFocus();
		setFocusNoNotify(c);
		focused.gotFocus();
	}

	@Override
	public void lostFocus() {
		if(focused!=null)
			focused.lostFocus();
		
		super.lostFocus();
	}
	
	@Override
	public void gotFocus() {
		if(focused!=null)
			focused.gotFocus();
		else
			cycleFocus();
		
		super.gotFocus();
	}
	
	@Override
	public void processEvent(UiEvent e){
		if(focused!=null) focused.processEvent(e);
	}
	
	protected Rectangle stickInside(Rectangle r){
		int line  = Math.max(r.getLine(),0);
		int col   = Math.max(r.getCol(),0);
		int line1 = Math.min(r.getLine()+r.getLines(),getSize().getLines()-1);
		int col1  = Math.min(r.getCol()+r.getCols(),getSize().getCols()-1);
		
		return new Rectangle(line,col,Math.abs(line1-line),Math.abs(col1-col));
	}
	
	protected synchronized Enumeration<T> rchildren(){
		Vector<T> rlist = new Vector<T>(childComponents);
		Collections.reverse(rlist);
			return rlist.elements();
	}
	
	public synchronized Enumeration<T> children(){
		return childComponents.elements();
	}
	
	public synchronized boolean hasChild(Component c){
		return childComponents.contains(c);
	}
	
	/**
	 * If possible, returns r translated to the origin (0,0),
	 * else null.
	 * 
	 * Redraw events are sent relative to the sender Component
	 * The location to be redrawn must computed through the 
	 * component tree.
	 * 
	 * @param r
	 * @param c
	 * @return
	 */
	public synchronized final Rectangle convertPosition(Rectangle r,Component c){
		
		//me: no change
		if(c==this) return r;
		
		//one of my children: I can convert
		if(hasChild(c)) return r.moveOf(c.getPosition());
		
		//else, ask all my child containers if they can convert
		Enumeration<T> ec = children();
		while(ec.hasMoreElements()){
			Component subc = ec.nextElement();
			if(subc instanceof Container<?>){
				Rectangle rconverted = ((Container<?>)subc).convertPosition(r,c);
				if(rconverted!=null) return rconverted.moveOf(subc.getPosition());
			}
		}
		
		//Not i my subtree, I cannot convert.
		return null;
	}
	
	/**
	 * Brigde to convert from position to position, instead of from Rectangle to Rectangle.
	 * @param p
	 * @param c
	 * @return
	 */
	public final Position convertPosition(Position p,Component c){
	try{
			
		return convertPosition( new Rectangle(p, Dimension.UNITY),c).getPosition();
			
	}catch(NullPointerException e){}
		
		return null;
	}
	
	@Override
	public synchronized void refresh() {
		
		Enumeration<T> ec = children();
		while(ec.hasMoreElements())
			ec.nextElement().refresh();
		
		super.refresh();
	}
	
	@Override
	public synchronized void resetColorManager(){
		
		Enumeration<T> children = children();
		while(children.hasMoreElements())
			children.nextElement().resetColorManager();
		
		super.resetColorManager();
	}

}
