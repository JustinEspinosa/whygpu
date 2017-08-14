package com.github.justinespinosa.textmode.curses;

import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.application.ApplicationFactory;

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
