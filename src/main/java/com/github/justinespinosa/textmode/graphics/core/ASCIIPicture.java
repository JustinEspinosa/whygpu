package com.github.justinespinosa.textmode.graphics.core;

import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public class ASCIIPicture {
	private ColorChar[][] data;
	private Dimension size;

	public ASCIIPicture(Dimension originalSize,Resolution res){
		computeSize(originalSize,res);
		data = new ColorChar[size.getLines()][size.getCols()];
	}
	
	private void computeSize(Dimension originalSize,Resolution res){
		size = res.toChars(originalSize);
	}
	
	public Dimension size(){
		return size;
	}
	
	public void set(Position p,ColorChar c){
		p.setAt(data, c);
	}
	
	public ColorChar get(Position p){
		return p.getAt(data);
	}
}
