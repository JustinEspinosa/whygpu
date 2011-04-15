package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;



/**
 * Yeah, yeah fuck it.
 * @author justin
 * 
 */
public class MultiLineTextField extends AbstractTextField {

	private Vector<StringBuilder> textContent = new Vector<StringBuilder>();
	private int cursorCol  = 0;
	private int cursorLine = 0;
	private boolean readOnly = false;
	
	public MultiLineTextField(int sLine, int sCol) {
		super(sLine, sCol, 1, 1);
		clear();
		textContent.add(new StringBuilder());
		drawContent();
	}
	
	private boolean cursorLineValid(){
		return (cursorLine>=0) && (cursorLine<textContent.size());
	}
	private boolean cursorColValid(){
		if(cursorLineValid())
			return (cursorCol>=0) && (cursorCol<=textContent.elementAt(cursorLine).length());
		else
			return false;
	}
	private void checkCursorCol(){
		if(cursorCol<0)
			cursorCol = 0;
			
		if(cursorCol>textContent.elementAt(cursorLine).length())
			cursorCol = textContent.elementAt(cursorLine).length();
		
	}
	private void checkCursorLine(){
		if(cursorLine<0)
			cursorLine = 0;
		
		if(cursorLine>textContent.size()-1) 
			cursorLine = textContent.size()-1;
	}
	private void checkCursorPosition(){
		checkCursorLine();
		checkCursorCol();
	}
	private void eraseCursor(){
		if(cursorColValid()){
			ColorChar cc = getCharAt(cursorLine, cursorCol);
		
			if(isCursorOn())
				setChar(cursorLine, cursorCol, cc.getChr());
		}
	}	
	private void drawCursor(){
		checkCursorPosition();
		ColorChar cc = getCharAt(cursorLine, cursorCol);
		
		if(isCursorOn()){
			setColor(cc.getColors().invert());
			setChar(cursorLine, cursorCol, cc.getChr());
			setColor(ColorDefaults.getDefaultColor(ColorType.EDIT));
		}
	}
	
	protected void drawContent(){
		int cLine = 0;
		Enumeration<StringBuilder> lines = textContent.elements();
		clear();
		setSize(new Dimension(1,1));
		
		while(lines.hasMoreElements()){
			StringBuilder line = lines.nextElement();
			if(getSize().getCols()<line.length()+1)
				setSize(new Dimension(getSize().getLines(),line.length()+1));
			
			printAt(cLine++, 0, line.toString());
			
			if(lines.hasMoreElements())
				setSize(getSize().vertical(1));
		}
		
		drawCursor();
	}
	
	private void deleteChar(){
		checkCursorPosition();
		if(cursorCol>0){
			textContent.elementAt(cursorLine).deleteCharAt(--cursorCol);
		}else{
			if(cursorLine>0){
				textContent.remove(cursorLine--);
				cursorCol = textContent.elementAt(cursorLine).length();
			}
		}
		
	}
	
	/* replace mode is for the future.. */
	private void insertChar(char c){
		checkCursorPosition();
		textContent.elementAt(cursorLine).insert(cursorCol++, c);
	}
	private void inserLine(){
		checkCursorPosition();
		StringBuilder line = textContent.elementAt(cursorLine);
		textContent.insertElementAt(new StringBuilder(line.toString().substring(0,cursorCol)),cursorLine);
		cursorLine++;
		line.delete(0,cursorCol);
		cursorCol=0;
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
		notifyDisplayChange();
		
	}
	
	public Position getCursorPosition(){
		return new Position(cursorLine,cursorCol);
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

		while(lines.hasMoreElements())
			strBld.append(lines.nextElement()+"\n");
		
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
		if(cursorCol>0){
			cursorCol--;
		}else{
			if(cursorLine>0){
				cursorLine--;
				cursorEnd();
			}
		}
	}
	private void cursorRight(){
		if(cursorCol<textContent.elementAt(cursorLine).length()){
			cursorCol++;
		}else{
			if(cursorLine<textContent.size()-1){
				cursorLine++;
				cursorCol = 0;
			}
		}
	}
	private void cursorEnd(){
		
			cursorCol = textContent.elementAt(cursorLine).length();
	}

	private void cursorUp(){
		if(cursorLine>0){
			cursorLine--;
			if(cursorCol>textContent.elementAt(cursorLine).length())
				cursorEnd();
		}
	}
	private void cursorDown(){
		
		if(cursorLine<textContent.size()-1){
			cursorLine++;
			if(cursorCol>textContent.elementAt(cursorLine).length())
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
		notifyDisplayChange();
	}
	
	protected final void cursorTo(Position d){
		eraseCursor();
		cursorLine = d.getLine();
		cursorCol  = d.getCol();
		drawCursor();
		notifyPositionChanged();
		notifyDisplayChange();
	}
	
	public void setReadOnly(boolean ro){
		readOnly = ro;
	}
	
	@Override
	public void processEvent(UiEvent e) {
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
		super.processEvent(e);
	}
	
}
