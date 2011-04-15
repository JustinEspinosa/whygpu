package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.Rectangle;
import fun.useless.curses.ui.event.EventReceiver;
import fun.useless.curses.ui.event.UiEvent;


public class Container<T extends Component> extends Component{
	private Vector<T> childComponents = new Vector<T>();
	private T focused = null;
	
	/**
	 * TODO Not good because it skips the check the prevent to draw outsized.
	 *      implement a real solution for this problem
	 */
	protected boolean transparent = false;

	public Container(int sLine,int sCol,int lines,int cols){
		super(sLine,sCol,lines,cols);
	}
	

	public void bringToFront(T c){
		bringToFront(c,true);
	}
	//Slow if lots of children
	public void bringToFront(T c, boolean focus){
		synchronized( childComponents ){
			int i = childComponents.indexOf(c);
			if(i >= 0 ){
				childComponents.remove(i);
				intAddChild(c);
				if(focus)
					setFocus(c);
			}
		}
	}
	
	protected final T getFocused(){
		return focused;
	}
	
	public int getChildCount(){
		synchronized( childComponents ){
			return childComponents.size();
		}
	}

	public T lastChild(){
		synchronized( childComponents ){
			return childComponents.lastElement();
		}
	}
	
	public T firstChild(){
		synchronized( childComponents ){
			return childComponents.firstElement();
		}
	}
	
	protected final int getIndexOf(T comp){
		synchronized( childComponents ){
			if(childComponents.contains(comp))
				return childComponents.indexOf(comp);
			else
				return -1;
		}
	}
	
	protected final T getAtIndex(int index){
		synchronized( childComponents ){
			try{
				return childComponents.elementAt(index);
			}catch(ArrayIndexOutOfBoundsException aioobe){
				return null;
			}
		}
	}
	
	protected void intRemoveChild(T c){
		synchronized( childComponents ){
			childComponents.remove(c);
		}
		c.setEventReceiver(null);
	}
	protected void intAddChild(T c){
		synchronized( childComponents ){
			childComponents.add(c);
		}
		c.setEventReceiver(getEventReceiver());
	}
	
	private void focusNextFocusable(int i){
		
		if(i>=getChildCount()) i=0;
		
		for(int n = i;n<getChildCount();n++){
			if(childComponents.elementAt(n).acceptsFocus()){
				setFocus(childComponents.elementAt(n));
				return;
			}
		}
	}
	public void cycleFocus(){
		synchronized( childComponents ){
			if(focused == null){
				focusNextFocusable(0);
				return;
			}
			int idx = childComponents.indexOf(focused);
			if(idx > -1){
				focusNextFocusable(idx+1);
			}
		}
		
	}
	
	@Override
	protected void setEventReceiver(EventReceiver l){
		Enumeration<T> ec = children();
		while(ec.hasMoreElements())
			ec.nextElement().setEventReceiver(l);
		super.setEventReceiver(l);
	}
	
	@Override
	public ColorChar getCharAt(int line,int col){
		
		ColorChar cc = null;
		ColorChar myCc = super.getCharAt(line, col);
	
		//don't draw children if i'm not under them
		if(myCc!=null || transparent)
			synchronized( childComponents ){
				for( int z = childComponents.size() - 1 ; z >= 0 && cc==null; z--){
					T c = childComponents.elementAt(z);
					if(c.isVisible()) 
						cc = c.getCharAt(line - c.getPosition().getLine(), col - c.getPosition().getCol());
				}
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
		int line = Math.max(r.getLine(),0);
		int col = Math.max(r.getCol(),0);
		int line1 = Math.min(r.getLine()+r.getLines(),getSize().getLines()-1);
		int col1 = Math.min(r.getCol()+r.getCols(),getSize().getCols()-1);
		
		return new Rectangle(line,col,Math.abs(line1-line),Math.abs(col1-col));
	}
	
	private Rectangle convertPositionOfChild(Rectangle r,Component c){
		int line = r.getLine() + c.getPosition().getLine();
		int col = r.getCol() + c.getPosition().getCol();
		int lines = r.getLines();
		int cols = r.getCols();
		
		return new Rectangle(line, col, lines, cols);
	}
	
	protected Enumeration<T> children(){
		synchronized( childComponents ){
			return childComponents.elements();
		}
	}
	
	public boolean hasChild(Component c){
		synchronized( childComponents ){
			return childComponents.contains(c);
		}
	}
	
	public final Rectangle convertPosition(Rectangle r,Component c){
		
		if(c==this) return r;
		
		if(hasChild(c))
			return convertPositionOfChild(r,c);
		
		/* The target is not me nor one of my children: search in children */
		Enumeration<T> ec = children();
		while(ec.hasMoreElements()){
			Component subc = ec.nextElement();
			if(subc instanceof Container<?>){
				Rectangle rr = ((Container<?>)subc).convertPosition(r,c);
				/* this child has it. now np is relative to it */
				if(rr!=null){
					return convertPositionOfChild(rr,subc);
				}
			}
		}
		
		return null;
	}
	
	public final Position convertPosition(Position p,Component c){
		Rectangle r = convertPosition(new Rectangle(p.getLine(), p.getCol(), 0, 0),c);
		
		if(r==null) return null;
		
		return r.getPosition();
	}

}
