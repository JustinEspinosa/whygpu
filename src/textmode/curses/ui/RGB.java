package textmode.curses.ui;

import textmode.curses.ui.util.Vector3;

class RGB extends Vector3{
	private int index;
	RGB(int r,int g,int b,int idx){
		super(r,g,b);
		index=idx;
	}

	public int index(){
		return index;
	}

	@Override
	public String toString() {
		return "R:"+x()+";G:"+y()+";B:"+z();
	}
}