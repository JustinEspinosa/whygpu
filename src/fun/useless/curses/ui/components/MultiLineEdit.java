package fun.useless.curses.ui.components;

import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.event.PositionChangeListener;
import fun.useless.curses.ui.event.PositionChangedEvent;

public class MultiLineEdit extends ScrollPane<MultiLineTextField> implements PositionChangeListener{

	public MultiLineEdit( int sLine, int sCol, int lines, int cols) {
		super(new MultiLineTextField(0, 0), sLine, sCol, lines, cols);
		getComponent().addPositionChangeListener(this);
		setColor(ColorDefaults.getDefaultColor(ColorType.EDIT));
		clear();
		notifyDisplayChange();
	}

	@Override
	public void positionChanged(PositionChangedEvent e) {
		if(e.getSource() == getComponent()){
			scrollToView(getComponent().getCursorPosition());
		}
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
}
