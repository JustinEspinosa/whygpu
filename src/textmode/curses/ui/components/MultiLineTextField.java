package textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.event.CharacterCodeEvent;
import textmode.curses.ui.event.TermKeyEvent;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;




/**
 * Yeah, yeah fuck it.
 * @author justin
 * 
 */
public class MultiLineTextField extends AbstractTextField {

	private Vector<StringBuilder> textContent = new Vector<StringBuilder>();
	private Position cursorLoc = Position.ORIGIN.copy();
	private boolean readOnly = false;
	
	public MultiLineTextField(Curses cs,Position p) {
		super(cs,p,new Dimension(1, 1) );
		clear();
		textContent.add(new StringBuilder());
		drawContent();
	}
	
	private int cursorLine(){
		return cursorLoc.getLine();
	}
	private int cursorCol(){
		return cursorLoc.getCol();
	}
	
	private boolean cursorLineValid(){
		return (cursorLine()>=0) && (cursorLine()<textContent.size());
	}
	
	private boolean cursorColValid(){
		if(cursorLineValid())
			return (cursorCol()>=0) && (cursorCol()<=textContent.elementAt(cursorLine()).length());
		else
			return false;
	}
	
	private boolean cursorPositionValid(){
		return  cursorLineValid() && cursorColValid();
	}
	
	private void checkCursorCol(){
		int col = cursorCol();
		
		if(cursorCol()<0)
			col = 0;
			
		if(cursorCol()>textContent.elementAt(cursorLine()).length())
			col = textContent.elementAt(cursorLine()).length();
		
		if(col!=cursorCol())
			cursorLoc = cursorLoc.withNewCol(col);
	}
	
	private void checkCursorLine(){
		int line = cursorLine();
		
		if(cursorLine()<0)
			line = 0;
		
		if(cursorLine()>textContent.size()-1) 
			line = textContent.size()-1;
		
		if(line!=cursorLine())
			cursorLoc = cursorLoc.withNewLine(line);
	}
	
	private void checkCursorPosition(){
		checkCursorLine();
		checkCursorCol();
	}
	
	private synchronized void eraseCursor(){
		if(cursorPositionValid()){
			
			ColorChar cc = getCharAt( cursorLoc );
		
			if(isCursorOn())
				setChar( cursorLoc, cc.getChr() );
		}
	}
	
	private synchronized void drawCursor(){
		checkCursorPosition();
		
		if(isCursorOn()){
			ColorChar cc = getCharAt( cursorLoc ).copy();
			cc.setColor(cc.getColors().invert());
			setChar(cursorLoc, cc);
		}
	}
	
	
	protected synchronized void drawContent(){
		int cLine = 0;
		Enumeration<StringBuilder> lines = textContent.elements();
		
		setSize(new Dimension(getLineCount()+1,getLineWidth()+1));
		clear();
		
		while(lines.hasMoreElements()){
			StringBuilder line = lines.nextElement();
			
			printAt(cLine, 0, line.toString());
			++cLine;
		}
		
		drawCursor();
	}
	
	private void deleteChar(){
		checkCursorPosition();
		
		if(cursorCol()>0){
			cursorLoc = cursorLoc.horizontal(-1);
			textContent.elementAt(cursorLine()).deleteCharAt(cursorCol());
		}else{
			if(cursorLine()>0){
				StringBuilder line = textContent.elementAt(cursorLine());
				textContent.remove(cursorLine());
				cursorLoc = new Position(cursorLine()-1,textContent.elementAt(cursorLine()).length());
				textContent.elementAt(cursorLine()).append(line);
			}
		}
		
	}
	
	/* replace mode is for the future.. */
	private void insertChar(char c){
		checkCursorPosition();
		textContent.elementAt(cursorLine()).insert(cursorCol(), c);
		cursorLoc = cursorLoc.horizontal(1);
	}
	
	private void inserLine(){
		checkCursorPosition();
		
		StringBuilder line = textContent.elementAt(cursorLine());
		
		textContent.insertElementAt(new StringBuilder(line.toString().substring(0,cursorCol())),cursorLine());
		
		line.delete(0,cursorCol());
		
		cursorLoc = new Position(cursorLine()+1,0);
	}
	
	protected final void editText(char c){

		/* Some characters perform special actions */
		switch(c){
		/*currently DEL/BS handled the same. See if I need to change it */
		case 127:
		case 8:
			deleteChar(); break;
		case 13:
			inserLine(); break;
		default:
			if(isPrintable(c)) insertChar(c); 
		}
		
		drawContent();
		notifyTextChanged();
		notifyPositionChanged();
		
	}
	
	public Position getCursorPosition(){
		return cursorLoc.copy();
	}

	@Override
	public void setText(String text) {
		textContent.clear();
		String[] lines =text.split("[\\r\\n]{1,2}");
		for(int n = 0;n < lines.length; n++)
			textContent.add(new StringBuilder(lines[n]));
		drawContent();
	}

	@Override
	public String getText() {
		StringBuilder strBld = new StringBuilder();
		Enumeration<StringBuilder> lines = textContent.elements();

		while(lines.hasMoreElements()){
			strBld.append(lines.nextElement().toString());
			strBld.append('\n');
		}
		
		return strBld.toString();
	}
	
	public void appendText(String text){
		textContent.lastElement().append(text);
		drawContent();
	}
	public void appendLine(String line){
		textContent.add(new StringBuilder(line));
		drawContent();
	}

	private void cursorLeft(){
		if(cursorCol()>0){
			cursorLoc = cursorLoc.horizontal(-1);
		}else{
			if(cursorLine()>0){
				cursorLoc = cursorLoc.vertical(-1);
				cursorEnd();
			}
		}
	}
	private void cursorRight(){
		if(cursorCol() <textContent.elementAt(cursorLine() ).length()){
			cursorLoc = cursorLoc.horizontal(1);
		}else{
			if(cursorLine()<textContent.size()-1)
				cursorLoc = new Position(cursorLine()+1,0);
		}
	}
	private void cursorEnd(){
		
			cursorLoc = cursorLoc.withNewCol(textContent.elementAt(cursorLine()).length());
	}

	private void cursorUp(){

		if(cursorLine()>0){
			cursorLoc = cursorLoc.vertical(-1);
			if(cursorCol()>textContent.elementAt(cursorLine()).length())
				cursorEnd();
		}
		
	}
	private void cursorDown(){
		
		if(cursorLine() < textContent.size()-1){
			cursorLoc = cursorLoc.vertical(1);
			if(cursorCol()>textContent.elementAt(cursorLine()).length())
				cursorEnd();
		}
	}
	
	private void cursorMove(int k){
		eraseCursor();
		switch(k){
		case TermKeyEvent.LEFT_ARROW:
			cursorLeft();  break;
		case TermKeyEvent.RIGHT_ARROW:
			cursorRight(); break;
		case TermKeyEvent.UP_ARROW:
			cursorUp();    break;
		case TermKeyEvent.DOWN_ARROW:
			cursorDown();  break;
		}
		drawCursor();
		notifyPositionChanged();
	}
	
	protected final void cursorTo(Position d){
		eraseCursor();
		cursorLoc = d.copy();
		drawCursor();
		
		notifyPositionChanged();
		
	}
	
	private int getLineWidth(){
		int width=0;
		for(StringBuilder b: textContent)
			if(b.length()>width) width = b.length();
		return width;
	}
	public int getLineCount(){
		return textContent.size();
	}
	
	public void setReadOnly(boolean ro){
		readOnly = ro;
	}
	
	@Override
	public void processEvent(UiEvent e) {
		super.processEvent(e);

		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent && !readOnly){
				editText(((CharacterCodeEvent)uie.getOriginalEvent()).getChar());
				return;
			}
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				switch( k ){
				case TermKeyEvent.LEFT_ARROW:
				case TermKeyEvent.RIGHT_ARROW:
				case TermKeyEvent.UP_ARROW:
				case TermKeyEvent.DOWN_ARROW:
					cursorMove(k);  return;
				}
			}
		}
	}

	@Override
	protected void redraw() {
		clear();
		drawContent();
	}
	
}
