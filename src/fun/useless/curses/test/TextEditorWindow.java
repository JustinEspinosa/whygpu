package fun.useless.curses.test;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.MultiLineEdit;
import fun.useless.curses.ui.components.Window;

public class TextEditorWindow extends Window {

	private MultiLineEdit textContent;
	
	public TextEditorWindow(String title, Application app,Curses cs, Position position, Dimension dimension) {
		super(title, app,cs, position, dimension);
		
		Dimension d = getSize().vertical(-1);
		textContent = new MultiLineEdit(cs,new Position(1, 0),d);	
		addChild(textContent);
		
	}
	
	@Override
	protected void userResized() {
		textContent.setSize(getSize().vertical(-1));
	}

}
