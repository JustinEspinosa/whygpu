package com.github.justinespinosa.textmode.curses;

import java.io.IOException;

import com.github.justinespinosa.textmode.curses.application.Screen;
import com.github.justinespinosa.textmode.curses.application.ScreenFactory;
import com.github.justinespinosa.textmode.curses.term.Terminal;


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
