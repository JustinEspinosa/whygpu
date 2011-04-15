package fun.useless.curses.test;

import fun.useless.curses.application.Application;
import fun.useless.curses.application.ApplicationFactory;

public class TextEditorFactory extends ApplicationFactory {

	@Override
	public String getDisplayName() {
		return "Text Editor";
	}

	@Override
	public Application createInstance() {
		return new TextEditor();
	}

}
