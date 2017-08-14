package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public class Label extends Component {

	protected String text;
	
	public Label(String txt, Curses cs, Position p, Dimension d) {
		super(cs,p,d);
		text = txt;
		update();
	}
	
	protected final void update(){
		clear();
		printAt(0, 0, text);
		notifyDisplayChange();
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

	@Override
	protected synchronized void redraw() {
		update();
	}
	
}
