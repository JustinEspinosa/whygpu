package textmode.curses.ui.util;

import textmode.curses.lang.ColorChar;
import textmode.curses.ui.BaseColor;
import textmode.curses.ui.ColorPair;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;

public class CharacterScreenBuffer {
	private ColorChar[][] screen;
	private Dimension screenSize;
	
	public CharacterScreenBuffer(Dimension size) {
		screenSize = size;	
		screen = new ColorChar[screenSize.getLines()][screenSize.getCols()];
	}
	
	private void implSet(Position p, ColorChar cchar){
		screen[p.getLine()][p.getCol()] = cchar.copy();
	}
	
	public ColorPair colorAt(Position p){
		if( screenSize.includes(p) ){
			ColorChar cchar = screen[p.getLine()][p.getCol()];
			if(cchar!=null)
				return cchar.getColors();
		}
		return new ColorPair(BaseColor.Undefined,BaseColor.Undefined);
	}
	
	public boolean set(Position p, ColorChar cchar){
		
		if( screenSize.includes(p) ){
			ColorChar oldCchar = screen[p.getLine()][p.getCol()];
			if( ! cchar.equals(oldCchar) ){
				implSet(p,cchar);
				return true;
			}	
		}
		return false;
	}
	
	public ColorChar get(Position p){
		
		if( screenSize.includes(p) ){
			ColorChar c = screen[p.getLine()][p.getCol()];
			if(c!=null) return c;
		}
		
		return new ColorChar(' ',new ColorPair(BaseColor.Undefined,BaseColor.Undefined));
	}
	
	public Dimension getSize(){
		return screenSize.copy();
	}
	
	public void invalidate() {
		screen = new ColorChar[screenSize.getLines()][screenSize.getCols()];
	}
	
	
}
