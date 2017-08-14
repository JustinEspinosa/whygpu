package com.github.justinespinosa.textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.event.PositionChangeListener;
import com.github.justinespinosa.textmode.curses.ui.event.PositionChangedEvent;
import com.github.justinespinosa.textmode.curses.ui.event.TextChangeListener;
import com.github.justinespinosa.textmode.curses.ui.event.TextChangedEvent;



public abstract class AbstractTextField extends Component {

	
	private Vector<TextChangeListener> tcListeners = new Vector<TextChangeListener>();
	private boolean cursorOn = false;
	private Vector<PositionChangeListener> pcListeners = new Vector<PositionChangeListener>();
	
	public final static boolean isPrintable(char c){
		return ( c > 31 && c < 127);
	}
	
	public AbstractTextField(Curses cs, Position p, Dimension d) {
		super(cs,p,d);
	}
	
	protected abstract void drawContent();
	
	public abstract void setText(String text);
	public abstract String getText();
	
	protected final boolean isCursorOn(){
		return cursorOn;
	}
	protected final void setCursorOn(boolean on){
		cursorOn = on;
	}	
	
	public void addTextChangeListener(TextChangeListener l){
		tcListeners.add(l);
	}
	public void removeTextChangeListener(TextChangeListener l){
		tcListeners.remove(l);
	}
	protected final void notifyTextChanged(){
		TextChangedEvent e = new TextChangedEvent(this);
		Enumeration<TextChangeListener> en = tcListeners.elements();
		while(en.hasMoreElements())
			en.nextElement().textChanged(e);
	}
	public void addPositionChangeListener(PositionChangeListener l){
		pcListeners .add(l);
	}
	public void removePositionChangeListener(PositionChangeListener l){
		pcListeners.remove(l);
	}
	protected final void notifyPositionChanged(){
		PositionChangedEvent e = new PositionChangedEvent(this);
		Enumeration<PositionChangeListener> en = pcListeners.elements();
		while(en.hasMoreElements())
			en.nextElement().positionChanged(e);
	}
	
	@Override
	public void gotFocus() {
		setCursorOn(true);
		drawContent();
		super.gotFocus();
	}
	
	@Override
	public void lostFocus() {
		setCursorOn(false);
		drawContent();
		super.lostFocus();
	}	

}
