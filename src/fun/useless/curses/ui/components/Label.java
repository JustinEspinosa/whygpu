package fun.useless.curses.ui.components;

import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;

public class Label extends Component {

	protected String text;
	
	public Label(String txt,int sLine, int sCol, int lines, int cols) {
		super(sLine, sCol, lines, cols);
		text = txt;
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW));
		update();
	}
	
	protected final void update(){
		clear();
		printAt(0, 0, text);
		notifyDisplayChange();
	}
	
	public void setColor(ColorPair p){
		super.setColor(p);
		update();
	}
	
	public String getText(){
		return text;
	}
	
	public void setText(String txt){
		text = txt;
		update();
	}
	
	@Override
	public boolean acceptsFocus(){
		return false;
	}
	
}
