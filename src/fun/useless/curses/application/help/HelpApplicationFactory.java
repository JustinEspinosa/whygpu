package fun.useless.curses.application.help;

import fun.useless.curses.application.Application;
import fun.useless.curses.application.ApplicationFactory;

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
