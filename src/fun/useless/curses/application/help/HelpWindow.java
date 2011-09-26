package fun.useless.curses.application.help;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.MultiLineEdit;
import fun.useless.curses.ui.components.Window;

public class HelpWindow extends Window{

	private MultiLineEdit textContent;
	
	public HelpWindow(String title,Application app,Curses cs,Position p,Dimension d){
		super(title,app,cs,p,d);
		textContent = new MultiLineEdit(curses(),new Position(1, 0), d.vertical(-1));	
		addChild(textContent);
		
		textContent.setReadOnly(true);
		
		textContent.setText   (" Help.");
		textContent.appendLine(" =====");
		textContent.appendLine("");
		textContent.appendLine(" ----------------------------------------");
		textContent.appendLine(" Key sequences:");
		textContent.appendLine(" ----------------------------------------");
		textContent.appendLine(" ESC-x   |   Exit");
		textContent.appendLine(" ESC-m   |   Menu");
		textContent.appendLine(" ESC-ESC |   Cancel");
		textContent.appendLine(" ESC-r   |   Resize mode");
		textContent.appendLine(" ESC-p   |   Move (Position) mode");
		textContent.appendLine(" ENTER   |   Back to normal mode if in");
		textContent.appendLine("         |   move or in resize mode");
		textContent.appendLine(" ESC-s   |   Scroll mode");
		textContent.appendLine(" ESC-w   |   Close window");
		textContent.appendLine(" ESC-v   |   Next window");
		textContent.appendLine(" ESC-TAB |   Next application");
		textContent.appendLine(" TAB     |   Navigate in window controls");
		textContent.appendLine(" ----------------------------------------");
		
		notifyDisplayChange();
	}

	@Override
	protected void userResized() {
		textContent.setSize(getSize().vertical(-1));
	}
	

}
