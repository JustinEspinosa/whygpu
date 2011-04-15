package fun.useless.curses.ui;

public class ColorChar {

	private char chr;
	private int  color;
	private int  backColor;
	
	public ColorChar(char c,int col, int backCol){
		setChr(c);
		setColor(col);
		setBackColor(backCol);
	}

	public void setChr(char chr) {
		this.chr = chr;
	}

	public char getChr() {
		return chr;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setBackColor(int backColor) {
		this.backColor = backColor;
	}

	public int getBackColor() {
		return backColor;
	}
	 @Override
	public String toString() {
		 return new String(new char[]{chr});
	}
	 
	public ColorPair getColors(){
		return new ColorPair(color, backColor);
	}
}
