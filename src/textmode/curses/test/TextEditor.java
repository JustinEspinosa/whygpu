package textmode.curses.test;

import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.components.MenuItem;
import textmode.curses.ui.components.PopUp;
import textmode.curses.ui.event.ActionEvent;
import textmode.curses.ui.event.ActionListener;

public class TextEditor extends Application {

	@Override
	public void stop() {

	}

	@Override
	public void start() {
		buildMenu();
	}
	
	private void buildMenu(){
		MenuItem tmpItem;
		PopUp fMenu = getWindowManager().newPopUp(30);
		tmpItem = new MenuItem("New",curses());
		tmpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newWindow();
			}
		});
		
		fMenu.addItem(tmpItem);
		getMenuBar().addPopUp("File", fMenu);
	}
	
	private void newWindow(){
		TextEditorWindow newWin = new TextEditorWindow("New", this,curses(), getWindowManager().getNextWindowPosition(), new Dimension(15, 30));
		showWindow(newWin);	
	}

	@Override
	protected String name() {
		return "Edit";
	}

}
