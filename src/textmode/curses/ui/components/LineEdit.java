package textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.event.ActionEvent;
import textmode.curses.ui.event.ActionListener;
import textmode.curses.ui.event.CharacterCodeEvent;
import textmode.curses.ui.event.Event;
import textmode.curses.ui.event.EventReceiver;
import textmode.curses.ui.event.PositionChangeListener;
import textmode.curses.ui.event.PositionChangedEvent;
import textmode.curses.ui.event.RedrawEvent;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;


public class LineEdit extends Container<Component> implements PositionChangeListener, EventReceiver{

	private LineTextField textField;
	private Button leftArrow;
	private Button rightArrow;
	private Vector<ActionListener> aListeners = new Vector<ActionListener>();
	
	public LineEdit(Curses cs,Position p,int cols) {
		super(cs,p, new Dimension(1, cols));
		textField = new LineTextField(curses(),new Position(0,0));
		intAddChild(textField);
		setFocusNoNotify(textField);
		
		leftArrow = new Button("<",curses(),new Position(0,0),new Dimension(1,1));
		leftArrow.setVisible(false);
		intAddChild(leftArrow);
		rightArrow = new Button(">",curses(),new Position(0,cols-1),new Dimension(1,1));
		rightArrow.setVisible(false);
		intAddChild(rightArrow);
		textField.addPositionChangeListener(this);
		
		clear();
		
		disableChildrenEventSending();
		
		notifyDisplayChange();
	}
	
	private void disableChildrenEventSending(){
		textField.setEventReceiver(this);
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
	
	@Override
	protected void setEventReceiver(EventReceiver l) {
		super.setEventReceiver(l);
		disableChildrenEventSending();
	}
	private void arrowDisplay(){
		leftArrow .setVisible( (textField.getPosition().getCol()<0)  );
		rightArrow.setVisible( (textField.getPosition().getCol()+textField.getSize().getCols()>getSize().getCols()) );
	}
	
	private void scrollToCursor(){
		textField.setPosition( new Position(0,ScrollPane.getScrollOffset(textField.getCursorColumn(), textField.getPosition().getCol(), textField.getSize().getCols(), getSize().getCols())) );
	}

	public void setText(String text){
		textField.setText(text);
	}
	
	public void setReplacementChar(char c){
		textField.setReplacementChar(c);
	}
	
	public String getText(){
		return textField.getText();
	}

	public void positionChanged(PositionChangedEvent e) {
		if(e.getSource()==textField){
			scrollToCursor();
			arrowDisplay();
			notifyDisplayChange();
		}
	}
	
	/**
	 * TODO: I think i used this once for debugging reasons. can probably be deleted, it looks useless.
	 * 
	 * @see textmode.curses.ui.components.Container#getCharAt(textmode.curses.ui.Position)
	 */
	@Override
	public ColorChar getCharAt(Position p) {
		ColorChar cc = super.getCharAt(p);
		return cc;
	}

	public void receiveEvent(Event e) {
		if(e instanceof RedrawEvent){
			notifyDisplayChange();
		}else{
			sendEvent(e);
		}
	}
	
    @Override
    public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				if( ((CharacterCodeEvent)uie.getOriginalEvent()).getChar() == 13){
					notifyAction(new ActionEvent(this));
					return;
				}
			}
		}
		super.processEvent(e);
    }

	@Override
	protected synchronized void redraw() {
		clear();
	}
}
