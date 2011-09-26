package fun.useless.curses.application;

import java.io.IOException;

import fun.useless.curses.CursesFactory;
import fun.useless.curses.term.Terminal;

public class ScreenFactory {
	public Screen createScreen(Terminal t, CursesFactory cf){
		try {
			Screen myScr = new Screen(t, cf);
			return myScr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
