package fun.useless.curses.application;

import java.io.IOException;

import fun.useless.curses.term.Terminal;

public class ScreenFactory {
	public Screen createScreen(Terminal t){
		try {
			Screen myScr = new Screen(t);
			return myScr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
