package com.github.justinespinosa.textmode.curses.application;

import java.util.Enumeration;
import java.util.Vector;

import com.github.justinespinosa.textmode.curses.application.help.HelpApplicationFactory;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.components.MenuItem;
import com.github.justinespinosa.textmode.curses.ui.components.PopUp;
import com.github.justinespinosa.textmode.curses.ui.event.ActionEvent;
import com.github.justinespinosa.textmode.curses.ui.event.ActionListener;

/*
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.Button;
import fun.useless.curses.ui.components.Label;
import fun.useless.curses.ui.components.Window;
*/

public class RootApplication extends Application {

	/*
	private class AboutWindow extends Window{

		public AboutWindow(Position position) {
			super("About",RootApplication.this,RootApplication.this.curses(), position, new Dimension(4, 20));
			
			Label l = new Label("Java Terminal IO",curses(),new Position(1,1),new Dimension(1,18));
			Button ok = new Button("OK",curses(), new Position(2,4),12);
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			intAddChild(l);
			intAddChild(ok);
			
			setFocusNoNotify(ok);
		}
		
	}
	*/
	
	protected class ApplicationLauncher implements ActionListener{
		private ApplicationFactory factory;
		private ApplicationLauncher(ApplicationFactory f){
			factory = f;
		}
		public void actionPerformed(ActionEvent e) {
			Application a = factory.createInstance();
			a.begin(getWindowManager());
		}
		
	}
	
	private Vector<MenuItem>       submitedItems = new Vector<MenuItem>();
	/*private AboutWindow            about;*/
	private HelpApplicationFactory help = new HelpApplicationFactory();
	private PopUp                  applicationMenu;
	
	public RootApplication(){
	}	
	
	@Override
	public void stop() {
	}

	@Override
	public void start() {
		PopUp helpMenu = getWindowManager().newPopUp(20);
		applicationMenu = getWindowManager().newPopUp(30);
		
		MenuItem helpShortcuts   = new MenuItem(help.getDisplayName(),curses());
		helpShortcuts.addActionListener(new ApplicationLauncher(help));
		
		helpMenu.addItem(helpShortcuts);
		
		/*about = new AboutWindow(getWindowManager().getNextWindowPosition());*/
		/*
		MenuItem aboutmenu = new MenuItem("About",curses());
		aboutmenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWindow(about);
			}
		});
		
		applicationMenu.addItem(aboutmenu);*/
		
		getMenuBar().addPopUp("Applications", applicationMenu);
		getMenuBar().addPopUp("Help", helpMenu);
		
		Enumeration<MenuItem> submited = submitedItems.elements();
		while(submited.hasMoreElements())
			applicationMenu.addItem(submited.nextElement());
		
		submitedItems.clear();
			
	}
	
	@Override
	protected void defaultMenu() {}

	@Override
	protected String name() {
		return "Root";
	}
	
	protected void addAppMenu(MenuItem menu){
		if(applicationMenu!=null)
			applicationMenu.addItem(menu);
		else
			submitedItems.add(menu);
	}
	
	public void addToApplicationMenu(ApplicationFactory factory){
		MenuItem menu = new MenuItem(factory.getDisplayName(),curses());
		menu.addActionListener(new ApplicationLauncher(factory));
		addAppMenu(menu);
	}
	
	public void resize(Dimension d){
		
	}

}
