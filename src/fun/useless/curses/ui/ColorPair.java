package fun.useless.curses.ui;

public class ColorPair {
	private int fg;
	private int bg;
	public ColorPair(int fcol,int bcol){
		fg = fcol;
		bg = bcol;
	}
	public int getForeColor(){
		return fg;
	}
	public int getBackColor(){
		return bg;
	}
	public ColorPair invert(){
		return new ColorPair(bg, fg);
	}
}
