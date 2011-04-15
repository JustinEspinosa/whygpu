package fun.useless.curses.ui.components;


import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;

public class LineTextField extends AbstractTextField{

	private StringBuilder textContent = new StringBuilder();
	private int cursorCol = 0;
	
	public LineTextField(int sLine, int sCol) {
		super(sLine, sCol, 1, 1);
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
	private void eraseCursor(){
		if((cursorColValid())){
			ColorChar cc = getCharAt(0, cursorCol);
		
			if(isCursorOn())
				setChar(0, cursorCol, cc.getChr());
		}
	}	
	private void drawCursor(){
		checkCursorPosition();
		ColorChar cc = getCharAt(0, cursorCol);
		
		if(isCursorOn()){
			setColor(cc.getColors().invert());
			setChar(0, cursorCol, cc.getChr());
			setColor(ColorDefaults.getDefaultColor(ColorType.EDIT));
		}
	}
	
	protected final void drawContent(){
		clear();
		setSize( new Dimension(1,textContent.length()+1) );
		printAt(0, 0, textContent.toString());
		
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

}
