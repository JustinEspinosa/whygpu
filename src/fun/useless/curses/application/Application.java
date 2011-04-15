package fun.useless.curses.application;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.components.MenuBar;
import fun.useless.curses.ui.components.MenuItem;
import fun.useless.curses.ui.components.PopUp;
import fun.useless.curses.ui.components.Window;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;

/** 
 * Instance started by the window manager.
 * Can submit windows.
 * 
 * @author justin
 *
 */
public abstract class Application {
	private MenuBar menu;
	private Vector<Window> myWindows = new Vector<Window>();
	private WindowManager manager;
	
	/**
	 * Call to start an app
	 * @param man
	 */
	public final void begin(WindowManager man){
		manager = man;
		setMenuBar(manager.newMenuBar());

		manager.registerApplication(this);

		manager.activateApplication(this);
		
		start();

	}
	
	public final WindowManager getWindowManager(){
		return manager;
	}
	
	/**
	 * Called to stop an app.
	 */
	public final void end(){
		Enumeration<Window> eMyWin = (new Vector<Window>(myWindows)).elements();
		
		while(eMyWin.hasMoreElements())
			hideWindow(eMyWin.nextElement());
		
		stop();
		manager.unregisterApplication(this);
	}

	/**
	 * You have no choice but to stop.
	 */
	public abstract void stop();
	public abstract void start();
	public abstract String getName();
	
	
	public final void showWindow(Window w){
		if(!myWindows.contains(w)){
			myWindows.add(w);
			getWindowManager().manageWindow(w);
		}
	}
	public final void hideWindow(Window w){
		if(myWindows.contains(w)){
			myWindows.remove(w);
			getWindowManager().releaseWindow(w);
		}
	}
	
	void defaultMenu(){
		MenuItem exitItem = new MenuItem("Exit "+getName());
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				end();
			}
		});

		PopUp appMenu = getWindowManager().newPopUp(30);
		appMenu.addItem(exitItem);
		
		getMenuBar().addPopUp(getName(), appMenu);
	}
	
	private final void setMenuBar(MenuBar m){
		menu = m;
		defaultMenu();
	}
	
	public MenuBar getMenuBar(){
		return menu;
	}

	public Vector<Window> getWindows() {
		return myWindows;
	}
	
}
