package textmode.curses.test;

import textmode.curses.Curses;
import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.components.MultiLineEdit;
import textmode.curses.ui.components.Window;

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
