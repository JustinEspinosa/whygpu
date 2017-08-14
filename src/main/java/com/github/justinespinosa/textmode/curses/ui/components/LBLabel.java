package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public class LBLabel extends Label{

	private boolean selected = false;
	private String  fullText;
	
	public LBLabel(String txt, Curses cs, Position p, Dimension d) {
		super(txt, cs,p,d);
		fullText = text;
		formatText();
		deselect();
	}
	
	private void formatText(){
		int cols = getSize().getCols();
		
		if(fullText.length() > cols && cols>3)
			setText(fullText.substring(0,cols-3)+"...");
		else
			setText(fullText);
	}
	
	@Override
	protected void userResized() {
		formatText();
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
		
		formatText();
		
		super.redraw();
	}
	
}