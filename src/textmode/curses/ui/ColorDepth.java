package textmode.curses.ui;


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

}