package com.github.justinespinosa.textmode.curses.ui;


public enum BaseColor implements Color {
	Undefined;

	private RGB color = new RGB(-1,-1,-1,-1);
	
	public int index() {
		return color.index();
	}

	public RGB rgb() {
		return color;
	}
	
	public ColorDepth depth(){
		return ColorDepth.COL8;
	}
}
