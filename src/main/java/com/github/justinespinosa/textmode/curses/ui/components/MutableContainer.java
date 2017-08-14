package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public abstract class MutableContainer<T extends Component> extends Container<T> {

	public MutableContainer(Curses cs, Position p, Dimension d) {
		super(cs,p,d);
	}
	
	public void removeChildren(){
		intRemoveChildren();
	}
	public void removeChild(T c){
		intRemoveChild(c);
	}
	
	public void addChild(T c){
		intAddChild(c);
	}

}
