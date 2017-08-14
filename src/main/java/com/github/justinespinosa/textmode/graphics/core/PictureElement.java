package com.github.justinespinosa.textmode.graphics.core;

import com.github.justinespinosa.textmode.curses.ui.RGB;

public class PictureElement {
	private RGB color;
	
	public PictureElement(){
		this(0,0,0);
	}
	public PictureElement(int r, int g, int b){
		this(new RGB(r,g,b));
	}
	public PictureElement(RGB rgb){
		color = rgb;
	}

	public RGB color(){
		return color.dup();
	}
	public int green(){
		return color.g();
	}
	public int blue(){
		return color.b();
	}
	public int red(){
		return color.r();
	}
	
}
