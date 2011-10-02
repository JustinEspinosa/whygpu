package textmode.curses.ui;




public class XTermColor256 implements Color {

	
	private RGB color;
	
	public XTermColor256(int r,int g,int b){
		color = ColorTable.XTermColor256.findNearestIndex(new RGB(r,g,b,-1));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof XTermColor256)
			return index()==((XTermColor256)obj).index();
		
		return false;
	}
	
	public int index() {
		return color.index();
	}

	@Override
	public String toString() {
		return "XTerm256#"+index();
	}

	public RGB rgb() {
		return color;
	}
	
	public ColorDepth depth(){
		return ColorDepth.COL256;
	}
}
