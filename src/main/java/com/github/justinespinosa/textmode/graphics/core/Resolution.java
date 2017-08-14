package com.github.justinespinosa.textmode.graphics.core;

import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public class Resolution {
	private double pixelPerCharacter  = 1.0;
	public Resolution(double ppc){
		pixelPerCharacter = ppc;
	}
	public Position toChars(Position p){
		return new Position((int) ((double)p.getLine()/ppch()),(int)((double)p.getCol()/ppcw()));
	}
	public Position toPixels(Position p){
		return new Position((int) ((double)p.getLine()*ppch()),(int)((double)p.getCol()*ppcw()));
	}
	public Dimension toChars(Dimension d){
		return new Dimension((int) ((double)d.getLines()/ppch()),(int)((double)d.getCols()/ppcw()));		
	}
	public Dimension toPixels(Dimension d){
		return new Dimension((int) ((double)d.getLines()*ppch()),(int)((double)d.getCols()*ppcw()));		
	}
	public double ppcw(){
		return pixelPerCharacter;
	}
	public double ppch(){
		return pixelPerCharacter*2.5;
	}
	public Resolution scale(double factor){
		return new Resolution(factor+pixelPerCharacter);
	}
}
