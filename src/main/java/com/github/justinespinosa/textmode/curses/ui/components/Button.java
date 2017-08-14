package com.github.justinespinosa.textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.event.ActionEvent;
import com.github.justinespinosa.textmode.curses.ui.event.ActionListener;
import com.github.justinespinosa.textmode.curses.ui.event.CharacterCodeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.SignalReceiver;
import com.github.justinespinosa.textmode.curses.ui.event.Signaler;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiInputEvent;




public class Button extends Component implements SignalReceiver{

	private String cText;
	private String oText;
	private Boolean animationState = false;
	private Vector<ActionListener> aListeners = new Vector<ActionListener>();

	public Button(String txt, Curses cs, Position p, int cols) {
		this(txt,cs,p, new Dimension(1, cols));
	}
	
	public Button(String txt, Curses cs, Position p, Dimension d) {
		super(cs,p,d);
		oText = txt;
		cText = oText;
		update();
	}
	
	protected final void update(){
		clear();
		int col = getSize().getCols()/2 - cText.length()/2;
		printAt(0, col, cText);
		notifyDisplayChange();
	}
	
	public void removeActionListener(ActionListener l){
		aListeners.remove(l);
	}
	
	public void addActionListener(ActionListener l){
		aListeners.add(l);
	}
	
	protected final void notifyAction(ActionEvent e){
		Enumeration<ActionListener> eA = aListeners.elements();
		while(eA.hasMoreElements())
			eA.nextElement().actionPerformed(e);
	}
	
	public String getText(){
		return oText;
	}
	
	public void setText(String txt){
		oText = txt;
		cText = oText;
		update();
	}
	
	@Override
	public void gotFocus() {
		cText = "["+oText+"]";
		update();
		super.gotFocus();
	}
	@Override
	public void lostFocus() {
		cText = oText;
		update();
		super.lostFocus();
	}
	
	@Override
	public void setBorder(boolean border){}
	
	protected void push(){
		synchronized (animationState) {
			if(!animationState){
				setColor(getColor().invert());
				update();
			}
			animationState = true;
		}
		new Signaler(this, 200);
	}
	
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				char k =((CharacterCodeEvent)uie.getOriginalEvent()).getChar();
				if( k == 13 || k == ' ' ){
					push();
					notifyAction(new ActionEvent(this));
					return;
				}
			}
		}
		super.processEvent(e);
	}

	public void signalReceived() {
		synchronized (animationState) {

			if(animationState){
				setColor(getColor().invert());
				update();
				animationState = false;
			}
		}
	}

	@Override
	protected synchronized void redraw() {
		clear();
		update();
	}

}
