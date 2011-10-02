package textmode.curses.ui.event;

public class TermKeyEvent extends TerminalInputEvent{
	public static final int UP_ARROW = 0;
	public static final int DOWN_ARROW = 1;
	public static final int LEFT_ARROW = 2;
	public static final int RIGHT_ARROW = 3;
	
	public static final int F1 = 11;
	public static final int F2 = 12;
	public static final int F3 = 13;
	public static final int F4 = 14;
	public static final int F5 = 15;
	public static final int F6 = 16;
	public static final int F7 = 17;
	public static final int F8 = 18;
	public static final int F9 = 19;
	public static final int F10 = 20;
	public static final int F11 = 21;
	public static final int F12 = 22;
	
	public static final int MENU     = 90;
	public static final int RESIZE   = 91;
	public static final int MOVE     = 92;
	public static final int CLOSE    = 93;
	public static final int NEW      = 94;
	public static final int NEXT     = 95;
	public static final int NEXTAPP  = 96;
	public static final int SCROLL   = 97;
	public static final int HELP     = 98;
	public static final int CANCEL   = 99;
	public static final int EXIT     = 999;
	
	
	private int key;
	/**
	 * 
	 * @param source usually an InputStream
	 * @param seq the full escape sequence including ESC char
	 */
	public TermKeyEvent(Object source, int k){
		super(source);
		key = k;
	}
	/**
	 * 
	 * @return the full escape sequence including ESC char
	 */
	public int getKey(){
		return key;
	}
}
