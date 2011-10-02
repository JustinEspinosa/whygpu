package textmode.curses.application.help;

import textmode.curses.application.Application;
import textmode.curses.application.ApplicationFactory;

public class HelpApplicationFactory extends ApplicationFactory {

	@Override
	public String getDisplayName() {
		return "Help";
	}

	@Override
	public Application createInstance() {
		return new HelpApplication();
	}

}
