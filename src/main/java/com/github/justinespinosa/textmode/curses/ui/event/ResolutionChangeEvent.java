package com.github.justinespinosa.textmode.curses.ui.event;

import com.github.justinespinosa.textmode.curses.ui.Dimension;

public class ResolutionChangeEvent extends UiEvent {

	private Dimension size;

	public ResolutionChangeEvent(Object src, Dimension d) {
		super(src);
		size = d;
	}
	public Dimension size(){
		return size;
	}

}
