package com.github.justinespinosa.textmode.curses.application.help;

import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.components.Window;

public class HelpApplication extends Application {

	Window helpWin;
		
	public HelpApplication() {
	}

	@Override
	public void start() {
		if(helpWin==null){
			helpWin = new HelpWindow("Help", this,curses(), getWindowManager().getNextWindowPosition(), new Dimension(15, 30));
		}
		showWindow(helpWin);
	}

	@Override
	public void stop() {
	}

	@Override
	protected String name() {
		return "Help";
	}

}
