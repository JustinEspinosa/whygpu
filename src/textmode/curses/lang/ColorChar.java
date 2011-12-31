package textmode.curses.lang;

import textmode.curses.ui.ColorPair;

public class ColorChar {

	private char chr;
	private ColorPair cPair;
	
	public ColorChar(char c,ColorPair cp){
		setChr(c);
		setColor(cp);
	}

	public void setChr(char chr) {
		if(chr > 0x19 && chr < 0x80)
			this.chr = chr;
		else
			this.chr = '?';
	}

	public char getChr() {
		return chr;
	}

	public void setColor(ColorPair cp) {
		cPair = cp;
	}

	public ColorPair getColors() {
		return cPair;
	}
	
	public ColorChar copy(){
		return new ColorChar(chr,cPair);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ColorChar){
			ColorChar cobj = (ColorChar)obj;
			return chr == cobj.chr && cPair.equals(cobj.cPair);
		}
		return false;
	}
	
	@Override
	public String toString() {
		 return "'"+new String(new char[]{chr})+"', "+cPair;
	}

	
}
