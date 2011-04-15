package fun.useless.curses.application;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.application.help.HelpApplicationFactory;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.components.Button;
import fun.useless.curses.ui.components.Label;
import fun.useless.curses.ui.components.MenuItem;
import fun.useless.curses.ui.components.PopUp;
import fun.useless.curses.ui.components.Window;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;

public class RootApplication extends Application {

	private class AboutWindow extends Window{

		public AboutWindow(Position position) {
			super("About",RootApplication.this, position, new Dimension(4, 20));
			
			Label l = new Label("Java Terminal IO",1,1,1,18);
			Button ok = new Button("OK",2,4,12);
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			intAddChild(l);
			intAddChild(ok);
			
			setFocusNoNotify(ok);
		}
		
	}
	
	private class ApplicationLauncher implements ActionListener{
		private ApplicationFactory factory;
		private ApplicationLauncher(ApplicationFactory f){
			factory = f;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Application a = factory.createInstance();
			a.begin(getWindowManager());
		}
		
	}
	
	private Vector<MenuItem> submitedItems = new Vector<MenuItem>();
	private AboutWindow            about;
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
		
		MenuItem helpShortcuts   = new MenuItem(help.getDisplayName());
		helpShortcuts.addActionListener(new ApplicationLauncher(help));
		
		helpMenu.addItem(helpShortcuts);
		
		about = new AboutWindow(getWindowManager().getNextWindowPosition());
		
		MenuItem aboutmenu = new MenuItem("About");
		aboutmenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showWindow(about);
			}
		});
		
		applicationMenu.addItem(aboutmenu);
		
		getMenuBar().addPopUp("Applications", applicationMenu);
		getMenuBar().addPopUp("Help", helpMenu);
		
		Enumeration<MenuItem> submited = submitedItems.elements();
		while(submited.hasMoreElements())
			applicationMenu.addItem(submited.nextElement());
		
		submitedItems.clear();
			
	}
	
	@Override
	void defaultMenu() {}

	@Override
	public String getName() {
		return "Root";
	}
	
	public void addToApplicationMenu(ApplicationFactory factory){
		MenuItem menu = new MenuItem(factory.getDisplayName());
		menu.addActionListener(new ApplicationLauncher(factory));
		if(applicationMenu!=null)
			applicationMenu.addChild(menu);
		else
			submitedItems.add(menu);
	}

}
