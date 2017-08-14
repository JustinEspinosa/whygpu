package com.github.justinespinosa.textmode.curses.ui.components;


import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.event.CharacterCodeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.TermKeyEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiInputEvent;

public class LineTextField extends AbstractTextField{

	private StringBuilder textContent = new StringBuilder();
	private int cursorCol = 0;
	private int replacementChar = -1;
	
	public LineTextField(Curses cs, Position p) {
		super(cs,p,new Dimension(1, 1));
		drawContent();
	}
	
	protected final void cursorColTo(int d){
		eraseCursor();
		cursorCol = d;	
		drawCursor();
		notifyPositionChanged();
		notifyDisplayChange();
	}
	private boolean cursorColValid(){
		return (cursorCol>=0) && (cursorCol<=textContent.length());
	}
	private synchronized void eraseCursor(){
		if((cursorColValid())){
			ColorChar cc = getCharAt(new Position(0, cursorCol) );
		
			if(isCursorOn())
				setChar(0, cursorCol, cc.getChr());
		}
	}	
	private synchronized void drawCursor(){
		checkCursorPosition();
		ColorChar cc = getCharAt( new Position(0, cursorCol) );
		
		if(isCursorOn()){
			setColor(cc.getColors().invert());
			setChar(0, cursorCol, cc.getChr());
			setColor(colors().get(getClass()));
		}
	}
	
	private String repeat(char c, int times){
	    StringBuilder b = new StringBuilder();
	    for(int i=0;i < times;i++)
	        b.append(c);
	    return b.toString();
	}
	
	protected synchronized final void drawContent(){
		clear();
		setSize( new Dimension(1,textContent.length()+1) );
		if(replacementChar==-1){
			printAt(0, 0, textContent.toString());
		}else{
			printAt(0, 0, repeat((char)replacementChar,textContent.length()));
		}
		
		drawCursor();
	}
	
	private void checkCursorPosition(){
		if(cursorCol<0) cursorCol = 0;
		if(cursorCol>textContent.length()) cursorCol = textContent.length();
	}
	
	private void deleteChar(){
		checkCursorPosition();
		if(cursorCol>0)
			textContent.deleteCharAt(--cursorCol);
	}

	/* replace mode is for the future.. */
	private void insertChar(char c){
		checkCursorPosition();
		textContent.insert(cursorCol++, c);
	}

	
	protected final void editText(char c){
		/* Some characters perform special actions */
		switch(c){
		/*currently DEL/BS handled the same. See if I need to change it */
		case 127:
		case 8:
			deleteChar(); break;
		default:
			if(isPrintable(c)) insertChar(c); 
		}
		drawContent();
		notifyTextChanged();
		notifyPositionChanged();
		notifyDisplayChange();
		
	}
	
	public int getCursorColumn(){
		return cursorCol;
	}
	
	@Override
	public void setText(String text){
		textContent.replace(0, textContent.length(), text);
		cursorColTo(textContent.length());
		drawContent();
		notifyTextChanged();
		notifyPositionChanged();
		notifyDisplayChange();
	}

	@Override
	public String getText(){
		return textContent.toString();
	}
	
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				editText(((CharacterCodeEvent)uie.getOriginalEvent()).getChar());
				return;
			}
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				switch( ((TermKeyEvent)uie.getOriginalEvent()).getKey() ){
				case TermKeyEvent.LEFT_ARROW:
					cursorColTo(getCursorColumn()-1); return;
				case TermKeyEvent.RIGHT_ARROW:
					cursorColTo(getCursorColumn()+1); return;
				}
			}
		}
		super.processEvent(e);
	}

	public void setReplacementChar(char c) {
		replacementChar = (int)c;
	}

	@Override
	protected synchronized void redraw() {
		clear();
		drawContent();
	}

}
