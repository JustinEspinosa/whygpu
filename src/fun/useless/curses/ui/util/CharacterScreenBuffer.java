package fun.useless.curses.ui.util;

import fun.useless.curses.lang.ColorChar;
import fun.useless.curses.ui.BaseColor;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

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

	public void invalidate() {
		screen = new ColorChar[screenSize.getLines()][screenSize.getCols()];
	}
	
	
}
