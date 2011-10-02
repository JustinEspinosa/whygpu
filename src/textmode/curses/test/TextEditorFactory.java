package textmode.curses.test;

import textmode.curses.application.Application;
import textmode.curses.application.ApplicationFactory;

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
