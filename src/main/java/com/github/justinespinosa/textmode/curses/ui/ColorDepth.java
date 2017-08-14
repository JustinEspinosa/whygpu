package com.github.justinespinosa.textmode.curses.ui;


public enum ColorDepth{
	COL8,
	COL16,
	COL256;
	
	public static ColorDepth forNumCols(int numcolors){
		if(numcolors<16)
			return ColorDepth.COL8;
		
		if(numcolors<88)
			return ColorDepth.COL16;
		
		return ColorDepth.COL256;		
	}
	
	public static Color colorFromRGB(RGB r,ColorDepth depth){
		switch(depth){
		case COL8:
			return ColorTable.AnsiColor8.finder().find(ColorTable.AnsiColor8.findNearestIndex(r).index());
		case COL16:
			return ColorTable.AnsiColor16.finder().find(ColorTable.AnsiColor16.findNearestIndex(r).index());
		case COL256:
			return new XTermColor256(r);
		}
		return null;
	}

}