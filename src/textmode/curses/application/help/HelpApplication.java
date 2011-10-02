package textmode.curses.application.help;

import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.components.Window;

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
