package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.event.PositionChangeListener;
import textmode.curses.ui.event.PositionChangedEvent;

public class MultiLineEdit extends ScrollPane<MultiLineTextField> implements PositionChangeListener{

	public MultiLineEdit(Curses cs,Position p,Dimension d ) {
		super(new MultiLineTextField(cs,new Position(0,0)),cs, p,d);
		getComponent().addPositionChangeListener(this);
		clear();
		notifyDisplayChange();
	}

	public void positionChanged(PositionChangedEvent e) {
		if(e.getSource() == getComponent()){
			scrollToView(getComponent().getCursorPosition());
			notifyDisplayChange();
		}
	}
	
	public void srollToEnd(){
		getComponent().cursorTo(new Position(getComponent().getLineCount()-1,1));
	}

	public void setReadOnly(boolean ro){
		getComponent().setReadOnly(ro);
	}
	
	public void setText(String text) {
		getComponent().setText(text);
	}

	public String getText() {
		return getComponent().getText();
	}
	
	public void appendText(String text){
		getComponent().appendText(text);
	}
	
	public void appendLine(String line){
		getComponent().appendLine(line);
	}

	@Override
	protected  synchronized void redraw() {
		clear();
	}
}
