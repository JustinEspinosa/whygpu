package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.Rectangle;
import fun.useless.curses.ui.event.Event;
import fun.useless.curses.ui.event.EventReceiver;
import fun.useless.curses.ui.event.RedrawEvent;
import fun.useless.curses.ui.event.SizeChangeListener;
import fun.useless.curses.ui.event.UiEvent;


public abstract class Component {

	private ColorChar[][] content;
	private int pCol;
	private int pLine;
	private int pCols;
	private int pLines;
	private int fC=-1;
	private int bC=-1;
	private boolean hasBorder=false;
	private boolean isVisible=true;
	private EventReceiver evReceiver;
	private Vector<SizeChangeListener<Component>> szListeners = new Vector<SizeChangeListener<Component>>();
	private int minLines = -1;
	private int minCols  = -1;
	private int maxLines = -1;
	private int maxCols = -1;
	
	
	public Component(int sLine,int sCol,int lines,int cols){
		pCol = sCol;
		pLine = sLine;
		pCols = cols;
		pLines = lines;
		content = new ColorChar[pLines][pCols];
		init();
	}
	/**
	 * -1 means unspecified
	 * @param lines
	 * @param cols
	 */
	protected final void setMinSize(int lines,int cols){
		minLines = lines;
		minCols = cols;
	}
	/**
	 * -1 means unspecified
	 * @param lines
	 * @param cols
	 */
	protected final void setMaxSize(int lines,int cols){
		maxLines = lines;
		maxCols = cols;
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
		notifyDisplayChange(new Rectangle(0,0,pLines,pCols));
	}
	
	protected final void createArray(ColorChar[][] arr){
		for(int i=0;i<arr.length;i++) for(int j=0;j<arr[i].length;j++)
			arr[i][j]=new ColorChar(' ',fC,bC);	
	}
	protected void init(){
		createArray(content);
	}
	private synchronized void resizeContent(){
		ColorChar[][] newContent = new ColorChar[pLines][pCols];
		createArray(newContent);
		
		for(int i=0;i<content.length;i++) for(int j=0;j<content[i].length;j++)
			if( i<pLines && j<pCols )	
				newContent[i][j] = content[i][j];
		content = newContent;
		
	}
	
	protected void setChar(int line,int col,char c){
		setChar(line,col,c,false);
	}
	protected synchronized void setChar(int line,int col,char c, boolean allowborder){
		
		boolean withBorder = (line >= getInnerTop() && line <= getInnerBottom() && 
								col >= getInnerLeft() && col <= getInnerRight() );
		boolean withoutBorder = ( line >= 0 && line < pLines && col >= 0 && col <= pCols);
		
		
		if( (withBorder && allowborder) || withoutBorder ){
			content[line][col].setChr(c);
			content[line][col].setColor(fC);
			content[line][col].setBackColor(bC);
		}
	}
	
	protected void unBorder(){
		
		for(int col=0;col<pCols;col++){
			setChar(0,col,' ', true);
			setChar(pLines-1,col,' ', true);
		}
		for(int line=0;line<pLines;line++){
			setChar(line,0,' ', true);
			setChar(line,pCols-1,' ', true);
		}
		
	}
	protected void border(){
		
		for(int col=1;col<pCols-1;col++){
			setChar(0,col,'=', true);
			setChar(pLines-1,col,'=', true);
		}
		for(int line=1;line<pLines-1;line++){
			setChar(line,0,'I', true);
			setChar(line,pCols-1,'I', true);
		}

		setChar(0,0,'+', true);
		setChar(pLines-1,0,'+', true);
		setChar(0,pCols-1,'+', true);
		setChar(pLines-1,pCols-1,'+', true);
		
	}
	
	public void clear(){
		
		for(int line=0;line<pLines;line++) for(int col=0;col<pCols;col++)
			setChar(line,col,' ');

		if(hasBorder)
			border();
	}
	
	public synchronized ColorChar getCharAt(int line,int col){

		if(line >= 0 && line < pLines && col >= 0 && col < pCols)
			return content[line][col];
		
		return null;
	}
	
	protected  int getInnerRight(){
		return (pCols-1) - (hasBorder?1:0);
	}
	protected  int getInnerBottom(){
		return (pLines-1) - (hasBorder?1:0);
	}
	protected  int getInnerLeft(){
		return (hasBorder?1:0);
	}
	protected  int getInnerTop(){
		return (hasBorder?1:0);
	}
	public ColorChar[][] getPartialContent(int sLine,int sCol,int lines,int cols){
		if(lines<0 || cols<0 ) return new ColorChar[0][0];
		
		ColorChar[][] r = new ColorChar[lines][cols];
		
		for( int line = 0 ; line < lines; line++) for (int col = 0 ; col < cols ; col++)
				r[line][col] = getCharAt(line + sLine, col + sCol);
		
		return r;
	}
	
	protected final EventReceiver getEventReceiver(){
		return evReceiver;
	}
	
	public ColorChar[][] getContent(){
		return getPartialContent(0,0,pLines,pCols);
	}
	
	public Position getPosition(){
		return new Position(pLine,pCol);
	}
	
    public Dimension getSize(){
    	return new Dimension(pLines, pCols);
    }
	
	public void setBorder(boolean border){
		hasBorder = border;
	}
	

	public ColorPair getColor(){
		return new ColorPair(fC, bC);
	}
	
	public void setColor(ColorPair p){
		fC = p.getForeColor();
		bC = p.getBackColor();
	}
	
	
	public void setVisible(boolean v){
		isVisible = v;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public final void setPosition(Position p){

		/* Area to update */
		Position origin = new Position(Math.min(p.getLine(), pLine),Math.min(p.getCol(), pCol));
		Position end    = new Position(Math.max(p.getLine()+pLines, pLine+pLines),Math.max(p.getCol()+pCols, pCol+pCols));

		pCol = p.getCol();
		pLine = p.getLine();
		
		/* Rectangle relative to current position */
		notifyDisplayChange(new Rectangle(origin.vertical(-pLine).horizontal(-pCol),end.vertical(-pLine).horizontal(-pCol)));
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
		Dimension size = new Dimension(checkLines(sz.getLines()),checkCols(sz.getCols()));
		
		if(hasBorder) unBorder();
		
		/*compute what's to be redrawn*/
		int lines = Math.max(pLines, size.getLines() );
		int cols = Math.max(pCols, size.getCols() );
		
		pLines = size.getLines();
		pCols = size.getCols();
		
		resizeContent();
		userResized();
		if(hasBorder) border();
		notifySizeChanged();
		notifyDisplayChange(new Rectangle(0,0,lines,cols));
	}
	
	private int checkCols(int cols){
		return stickToMinMax(cols,minCols,maxCols);
	}
	
	private int checkLines(int lines){
		return stickToMinMax(lines,minLines,maxLines);
	}
	
	private int stickToMinMax(int val,int min, int max){
		if(min>-1) val = Math.max(min, val);
		if(max>-1) val = Math.min(max, val);
		return val;
	}
	
	public void processEvent(UiEvent e){
		
	}
	
	public void gotFocus(){
		notifyDisplayChange();
	}
	
	public void lostFocus(){
		notifyDisplayChange();
	}
	
	protected final void printAt(int line,int col, String str, boolean allowborder){
		for(int n=0;n<str.length();n++) setChar(line,col + n,str.charAt(n), allowborder);
	}
	public void printAt(int line,int col, String str){
		printAt(line,col,str,false);
	}
}
