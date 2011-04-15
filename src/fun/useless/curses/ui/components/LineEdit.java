package fun.useless.curses.ui.components;

import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.Event;
import fun.useless.curses.ui.event.EventReceiver;
import fun.useless.curses.ui.event.PositionChangeListener;
import fun.useless.curses.ui.event.PositionChangedEvent;
import fun.useless.curses.ui.event.RedrawEvent;

public class LineEdit extends Container<Component> implements PositionChangeListener, EventReceiver{

	private LineTextField textField;
	private Button leftArrow;
	private Button rightArrow;
	
	public LineEdit(int sLine, int sCol, int cols) {
		super(sLine, sCol, 1, cols);
		textField = new LineTextField(0, 0);
		intAddChild(textField);
		setFocusNoNotify(textField);
		
		leftArrow = new Button("<",0,0,1,1);
		leftArrow.setVisible(false);
		intAddChild(leftArrow);
		rightArrow = new Button(">",0,cols-1,1,1);
		rightArrow.setVisible(false);
		intAddChild(rightArrow);
		textField.addPositionChangeListener(this);
		
		setColor(ColorDefaults.getDefaultColor(ColorType.EDIT));
		clear();
		
		disableChildrenEventSending();
		
		
		
		notifyDisplayChange();
	}
	
	private void disableChildrenEventSending(){
		textField.setEventReceiver(this);
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
	public String getText(){
		return textField.getText();
	}

	@Override
	public void positionChanged(PositionChangedEvent e) {
		if(e.getSource()==textField){
			scrollToCursor();
			arrowDisplay();
			notifyDisplayChange();
		}
	}
	
	@Override
	public ColorChar getCharAt(int line, int col) {
		// TODO Auto-generated method stub
		ColorChar cc = super.getCharAt(line, col);
		return cc;
	}

	@Override
	public void receiveEvent(Event e) {
		if(e instanceof RedrawEvent){
			notifyDisplayChange();
		}else{
			sendEvent(e);
		}
	}
	

}
