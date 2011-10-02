package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;

public class LBLabel extends Label{

	private boolean selected = false;
	
	public LBLabel(String txt,Curses cs,Position p,Dimension d) {
		super(txt, cs,p,d);
		deselect();
	}
	
	public void deselect(){
		setColor(colors().get(getClass()));
		selected = false;
		update();
	}
	
	public void select(){
		setColor(colors().getAlt(getClass()));
		selected = true;
		update();
	}
	
	@Override
	protected synchronized void redraw() {
		if(selected) 
			setColor(colors().getAlt(getClass()));
		super.redraw();
	}
	
}