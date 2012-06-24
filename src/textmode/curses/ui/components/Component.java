package textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.lang.ColorChar;
import textmode.curses.ui.BaseColor;
import textmode.curses.ui.ColorPair;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.Rectangle;
import textmode.curses.ui.event.Event;
import textmode.curses.ui.event.EventReceiver;
import textmode.curses.ui.event.RedrawEvent;
import textmode.curses.ui.event.SizeChangeListener;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.look.ColorManager;



public abstract class Component {

	private ColorChar[][] content;
	private Position position;
	private Dimension size;
	private Curses curses;
	private ColorPair color = new ColorPair(BaseColor.Undefined,BaseColor.Undefined);
	private boolean hasBorder=false;
	private boolean isVisible=true;
	private EventReceiver evReceiver;
	private Vector<SizeChangeListener<Component>> szListeners = new Vector<SizeChangeListener<Component>>();
	
	private Dimension minSize = null;
	private Dimension maxSize = null;
	private ColorManager manager;
	
	
	public Component(Curses cs,Position p,Dimension d){
		position = p.copy();
		size = d.copy();
		curses = cs;
		content = new ColorChar[size.getLines()][size.getCols()];
		manager = cs.colors();
		init();
	}
	
	public final void setColorManager(ColorManager man){
		manager = man;
	}
	public void resetColorManager(){
		manager = curses().colors();
	}
	
	public final ColorManager colors(){
		return manager;
	}
	
	public Curses curses(){
		return curses;
	}
	
	/**
	 * 
	 * @param d
	 */
	public void setMinSize(Dimension d){
		minSize = d.copy();
	}
	/**
	 * 
	 * @param d
	 */
	public void setMaxSize(Dimension d){
		maxSize = d.copy();
	}
	
	public Dimension getMaxSize() {
		return maxSize.copy();
	}
	public Dimension getMinSize() {
		return minSize.copy();
	}
	
	protected final void notifySizeChanged(){
		Enumeration<SizeChangeListener<Component>> esz = szListeners.elements();
		while(esz.hasMoreElements())
			esz.nextElement().sizeChanged(this);
	}
	
	public void addSizeChangeListener(SizeChangeListener<Component> l){
		szListeners.add(l);
	}

	public void removeSizeChangeListener(SizeChangeListener<Component> l){
		szListeners.remove(l);
	}
	
	protected void setEventReceiver(EventReceiver l){
		evReceiver = l;
	}
	
	protected final void sendEvent(Event e){
		if(evReceiver!=null) 
			evReceiver.receiveEvent(e);
	}
	protected final void notifyDisplayChange(Rectangle r){
		sendEvent(new RedrawEvent(this, r));
	}
	protected final void notifyDisplayChange(){
		notifyDisplayChange(new Rectangle(Position.ORIGIN,size));
	}
	
	protected void createArray(ColorChar[][] arr){
		for(int i=0;i<arr.length;i++) for(int j=0;j<arr[i].length;j++)
			arr[i][j]=new ColorChar(' ',color);	
	}
	
	protected void init(){
		setColor(colors().get(getClass()));
		createArray(content);
	}
	
	private synchronized void resizeContent(){
		ColorChar[][] newContent = new ColorChar[size.getLines()][size.getCols()];
		createArray(newContent);
		
		for(int i=0;i<content.length;i++) for(int j=0;j<content[i].length;j++)
			if( i<size.getLines() && j<size.getCols() )	
				newContent[i][j] = content[i][j];
		content = newContent;
		
	}
	
	protected void setChar(int line,int col,char c){
		setChar(line,col,c,false);
	}
	
	protected void setChar(Position p,char c){
		setChar(p,c,false);
	}
	
	protected void setChar(Position p,ColorChar c){
		setChar(p,c,false);
	}
	
	protected synchronized void setChar(Position p,ColorChar c, boolean allowborder){
		int line = p.getLine(), col = p.getCol();
		boolean withBorder = (line >= getInnerTop() && line <= getInnerBottom() && 
				col >= getInnerLeft() && col <= getInnerRight() );
		boolean withoutBorder = ( line >= 0 && line < size.getLines() && col >= 0 && col <= size.getCols());
		
		
		if( (withBorder && allowborder) || withoutBorder )
			if(line<content.length)
				if(col<content[line].length)
					content[line][col] = c;

	}
	protected synchronized void setChar(Position p,char c, boolean allowborder){
		setChar(p,new ColorChar(c, getColor()),allowborder);
	}
	protected synchronized void setChar(int line,int col,char c, boolean allowborder){
		setChar(new Position(line,col),new ColorChar(c, getColor()),allowborder);
	}
	
	protected void unBorder(){
		
		for(int col=0;col<size.getCols();col++){
			setChar(0,col,' ', true);
			setChar(size.getLines()-1,col,' ', true);
		}
		for(int line=0;line<size.getLines();line++){
			setChar(line,0,' ', true);
			setChar(line,size.getCols()-1,' ', true);
		}
		
	}
	protected void border(){
		
		for(int col=1;col<size.getCols()-1;col++){
			setChar(0,col,'-', true);
			setChar(size.getLines()-1,col,'-', true);
		}
		for(int line=1;line<size.getLines()-1;line++){
			setChar(line,0,'|', true);
			setChar(line,size.getCols()-1,'|', true);
		}

		setChar(0,0,'+', true);
		setChar(size.getLines()-1,0,'+', true);
		setChar(0,size.getCols()-1,'+', true);
		setChar(size.getLines()-1,size.getCols()-1,'+', true);
		
	}
	
	public void clear(){
		
		Iterator<Position> i = getSize().iterator();
		
		while(i.hasNext())
			setChar(i.next(),' ');

		if(hasBorder)
			border();
	}
	
	/**
	 * Position is relative to the components origin (0:0 == top,left)
	 * @param p
	 * @return
	 */
	public synchronized ColorChar getCharAt(Position p){
		try{
		if(size.includes(p))
			return p.getAt(content);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	protected  int getInnerRight(){
		return (size.getCols()-1) - (hasBorder?1:0);
	}
	protected  int getInnerBottom(){
		return (size.getLines()-1) - (hasBorder?1:0);
	}
	protected  int getInnerLeft(){
		return (hasBorder?1:0);
	}
	protected  int getInnerTop(){
		return (hasBorder?1:0);
	}
	public synchronized ColorChar[][] getPartialContent(Position from, Dimension size){
		
		ColorChar[][] r = size.newArrayOf(ColorChar.class);
		Iterator<Position> i = size.iterator(from);
		
		while(i.hasNext()){
			Position p = i.next();
			try{
				ColorChar c = getCharAt(p);
				if(c==null) c = new ColorChar(' ', getColor());
						
				p.relativeTo(from).setAt(r, c);
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
		
		return r;
	}
	
	protected final EventReceiver getEventReceiver(){
		return evReceiver;
	}
	
	public ColorChar[][] getContent(){
		return getPartialContent(Position.ORIGIN,size);
	}
	
	public Position getPosition(){
		return position.copy();
	}
	
    public Dimension getSize(){
    	return size.copy();
    }
	
	public void setBorder(boolean border){
		hasBorder = border;
	}
	
	public boolean hasBorder(){
		return hasBorder;
	}

	public ColorPair getColor(){
		return color;
	}
	
	public void setColor(ColorPair p){
		color = p;
	}
	
	
	public void setVisible(boolean v){
		isVisible = v;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public final void setPosition(Position p){

		/* Area to update */
		Position origin = new Position(Math.min(p.getLine(), position.getLine()),
									   Math.min(p.getCol(),  position.getCol()));
		
		Position end    = new Position(Math.max(p.getLine()+size.getLines(), position.getLine()+size.getLines()),
									   Math.max(p.getCol() +size.getCols(),  position.getCol() +size.getCols()));

		position = p.copy();
				
		/* Rectangle relative to current position */
		notifyDisplayChange(new Rectangle(origin.relativeTo(position), end.relativeTo(position)) );
	}
	
	/**
	 * Called on resize. Override to get notified of a resize
	 */
	protected void userResized(){
		
	}
	
	public boolean acceptsFocus(){
		return true;
	}
	
	/**
	 * Resizes the component. Content that becomes invisible gets lost.
	 * @param lines
	 * @param cols
	 */
	public final void setSize(Dimension sz){
		if(sz.getLines()<0 || sz.getCols()<0) return;
		
		/*stick to max/min*/
		Dimension size2 = new Dimension(checkLines(sz.getLines()),checkCols(sz.getCols()));
		
		if(hasBorder) unBorder();
		
		/*compute what's to be redrawn*/
		int lines = Math.max(size.getLines(), size2.getLines() );
		int cols  = Math.max(size.getCols(), size2.getCols() );
		
		size = size2.copy();
		
		resizeContent();
		userResized();
		
		if(hasBorder) border();
		notifySizeChanged();
		notifyDisplayChange(new Rectangle(0,0,lines,cols));
	}
	
	private int checkCols(int cols){
		if(minSize==null || maxSize==null)
			return cols;
		return stickToMinMax(cols,minSize.getCols(),maxSize.getCols());
	}
	
	private int checkLines(int lines){
		if(minSize==null || maxSize==null)
			return lines;
		return stickToMinMax(lines,minSize.getLines(),maxSize.getLines());
	}
	
	private int stickToMinMax(int val,int min, int max){
		if(min>-1) val = Math.max(min, val);
		if(max>-1) val = Math.min(max, val);
		return val;
	}
	
	public void processEvent(UiEvent e){
		
	}
	
	public synchronized void gotFocus(){
		notifyDisplayChange();
	}
	
	public synchronized void lostFocus(){
		notifyDisplayChange();
	}
	
	protected synchronized final void printAt(int line,int col, String str, boolean allowborder){
		int ccol = col;
		for(int n=0;n<str.length();n++){
			char c = str.charAt(n);
			if(c=='\n'){
				++line; ccol = col;
			}else{
				setChar(line,ccol,str.charAt(n), allowborder);
				++ccol;
			}
		}
	}
	
	protected abstract void redraw();
	
	public synchronized void refresh(){
		setColor(colors().get(getClass()));
		redraw();
		notifyDisplayChange();
	}
	
	public synchronized void printAt(int line,int col, String str){
		printAt(line,col,str,false);
	}
}
