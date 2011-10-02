package textmode.curses.test;

import java.io.IOException;

import textmode.curses.CursesFactory;
import textmode.curses.application.Screen;
import textmode.curses.application.ScreenFactory;
import textmode.curses.term.Terminal;


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
