package fun.useless.curses.test;

import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.MultiLineEdit;
import fun.useless.curses.ui.components.Window;

public class TextEditorWindow extends Window {

	private MultiLineEdit textContent;
	
	public TextEditorWindow(String title, Application app, Position position, Dimension dimension) {
		super(title, app, position, dimension);
		
		Dimension d = getSize().vertical(-1);
		textContent = new MultiLineEdit(1, 0,d.getLines(),d.getCols());	
		addChild(textContent);
		
		notifyDisplayChange();
	}
	
	@Override
	protected void userResized() {
		textContent.setSize(getSize().vertical(-1));
	}

}
