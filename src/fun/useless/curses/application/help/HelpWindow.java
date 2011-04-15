package fun.useless.curses.application.help;

import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.MultiLineEdit;
import fun.useless.curses.ui.components.Window;

public class HelpWindow extends Window{

	private MultiLineEdit textContent;
	
	public HelpWindow(String title,Application app,Position position,Dimension dimension){
		this(title,app, position.getLine(),position.getCol(),dimension.getLines(),dimension.getCols());
	}
	public HelpWindow(String title,Application app, int sLine, int sCol, int lines, int cols) {
		super(title,app, sLine, sCol, lines, cols);
		textContent = new MultiLineEdit(1, 0, lines-1, cols);	
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
