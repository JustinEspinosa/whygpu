package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.SignalReceiver;
import fun.useless.curses.ui.event.Signaler;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;



public class Button extends Component implements SignalReceiver{

	private String cText;
	private String oText;
	private Boolean animationState = false;
	private Vector<ActionListener> aListeners = new Vector<ActionListener>();

	public Button(String txt,Curses cs,Position p, int cols) {
		this(txt,cs,p, new Dimension(1, cols));
	}
	
	public Button(String txt,Curses cs,Position p,Dimension d) {
		super(cs,p,d);
		oText = txt;
		cText = oText;
		setColor(ColorDefaults.getDefaultColor(ColorType.BUTTON,cs));
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

}
