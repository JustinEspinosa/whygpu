package fun.useless.curses.application.help;

import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.components.Window;

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
