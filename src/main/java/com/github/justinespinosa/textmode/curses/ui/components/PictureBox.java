package com.github.justinespinosa.textmode.curses.ui.components;

import java.util.Iterator;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.graphics.core.ASCIIPicture;

public class PictureBox extends Component {

	private ASCIIPicture picture;
	
	public PictureBox(Curses cs, Position p, Dimension d) {
		super(cs,p,d);
		clear();
	}
	public PictureBox(ASCIIPicture pic, Curses cs, Position p) {
		super(cs,p,pic.size());
		picture = pic;
		update();
	}
	
	protected final void update(){
		clear();

		Iterator<Position> i = picture.size().iterator();
		while(i.hasNext()){
			Position pos = i.next();
			setChar(pos,picture.get(pos).copy());
		}
		
		notifyDisplayChange();
	}
	
	public void setPicture(ASCIIPicture pic){
		setSize(pic.size());
		picture = pic;
		update();
	}
	
	@Override
	public boolean acceptsFocus(){
		return false;
	}

	@Override
	protected synchronized void redraw() {
		//update();
	}
	
}
