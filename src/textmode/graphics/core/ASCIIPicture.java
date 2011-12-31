package textmode.graphics.core;

import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;

public class ASCIIPicture {
	private ColorChar[][] data;
	private Dimension size;
	
	private static final char[] glyphs = {' ','.',',','-','+','*','%','$','#'};
	
	public static char getChar(double percFG){
		if(percFG>1) percFG=1;
		int index = (int) Math.floor(((double)(glyphs.length-1))*percFG);
		return glyphs[index];
	}
	
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
