package textmode.curses.ui.event;

import textmode.curses.ui.Rectangle;

public class RedrawEvent extends Event {

	private Rectangle area;
	public RedrawEvent(Object src, Rectangle r) {
		super(src);
		area = r;
	}
	
	public Rectangle getArea(){
		return area;
	}

}
