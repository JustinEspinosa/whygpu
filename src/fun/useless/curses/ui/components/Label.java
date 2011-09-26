package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

public class Label extends Component {

	protected String text;
	
	public Label(String txt,Curses cs,Position p,Dimension d) {
		super(cs,p,d);
		text = txt;
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW,curses()));
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
