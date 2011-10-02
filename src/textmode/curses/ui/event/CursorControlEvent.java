package textmode.curses.ui.event;

import textmode.curses.ui.Position;

public class CursorControlEvent extends Event {

	private boolean state;
	private Position pos;
	public CursorControlEvent(Object src, boolean cursorState,Position p) {
		super(src);
		state = cursorState;
		pos = p;
	}
	
	public boolean getCursorState(){
		return state;
	}
	public Position getPosition(){
		return pos;
	}

}
