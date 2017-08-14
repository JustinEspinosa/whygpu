package com.github.justinespinosa.textmode.curses.lang;

import java.util.ArrayList;
import java.util.List;


public class ColorStringBuilder {
	private List<ColorChar> data = new ArrayList<ColorChar>();
	
	public void append(ColorChar cChar){
		data.add(cChar);
	}
	
	public int length(){
		return data.size();
	}
	
	public ColorString toColorString(){
		return new ColorString(data);
	}
}
