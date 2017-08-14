package com.github.justinespinosa.textmode.curses.application.help;

import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.application.ApplicationFactory;

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
