package com.github.justinespinosa.textmode.curses.ui;

public class ColorPair {
	private Color fg;
	private Color bg;
	
	public ColorPair(Color fcol,Color bcol){
		fg = fcol;
		bg = bcol;
	}
	public Color getForeColor(){
		return fg;
	}
	public Color getBackColor(){
		return bg;
	}
	public ColorPair invert(){
		return new ColorPair(bg, fg);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ColorPair){
			ColorPair cp = (ColorPair)obj;
			return fg.equals(cp.fg) && bg.equals(cp.bg);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Fore: "+fg+", Back: "+bg;
	}
}
