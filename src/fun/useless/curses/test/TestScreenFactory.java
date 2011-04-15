package fun.useless.curses.test;

import java.io.IOException;

import fun.useless.curses.application.Screen;
import fun.useless.curses.application.ScreenFactory;
import fun.useless.curses.term.Terminal;

public class TestScreenFactory extends ScreenFactory {
	public Screen createScreen(Terminal t){
		try {
			Screen myScr = new Screen(t);
			
			myScr.submitApplication(new TextEditorFactory());
			
			return myScr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
