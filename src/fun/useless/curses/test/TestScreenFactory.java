package fun.useless.curses.test;

import java.io.IOException;

import fun.useless.curses.CursesFactory;
import fun.useless.curses.application.Screen;
import fun.useless.curses.application.ScreenFactory;
import fun.useless.curses.term.Terminal;

public class TestScreenFactory extends ScreenFactory {
	@Override
	public Screen createScreen(Terminal t, CursesFactory cf){
		try {
			Screen myScr = new Screen(t,cf);
			
			myScr.submitApplication(new TextEditorFactory());
			
			return myScr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
