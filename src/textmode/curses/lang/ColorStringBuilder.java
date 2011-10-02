package textmode.curses.lang;

import java.util.ArrayList;
import java.util.List;


public class ColorStringBuilder {
	private List<ColorChar> data = new ArrayList<ColorChar>(80);
	
	public void append(ColorChar cChar){
		data.add(cChar);
	}
	
	public ColorString toColorString(){
		return new ColorString(data);
	}
}
