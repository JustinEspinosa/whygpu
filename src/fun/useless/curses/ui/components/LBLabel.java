package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

class LBLabel extends Label{

	public LBLabel(String txt,Curses cs,Position p,Dimension d) {
		super(txt, cs,p,d);
		deselect();
	}
	
	public void deselect(){
		setColor(ColorDefaults.getDefaultColor(ColorType.BUTTON,curses()));
		update();
	}
	
	public void select(){
		setColor(ColorDefaults.getDefaultColor(ColorType.SELECTED,curses()));
		update();
	}
	
}