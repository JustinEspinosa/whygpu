package fun.useless.curses.application;

import java.io.IOException;

import fun.useless.curses.Curses;
import fun.useless.curses.CursesFactory;
import fun.useless.curses.term.Terminal;
import fun.useless.curses.ui.WindowManager;


public class Screen extends Thread{

	private Curses        curses;
	private WindowManager winMan;
	
	public Screen(Terminal t, CursesFactory cf) throws IOException{
		this(t,cf,null);
	}
	
	public Screen(Terminal t,CursesFactory cf, RootApplication root) throws IOException{
		curses = cf.createCurses(t);
		curses.noecho();
		curses.raw();
		curses.initColor();
		curses.civis();
		//curses.setUtf8(true);
		winMan = new WindowManager(curses, root);
	}
	
	protected WindowManager getWindowManager(){
		return winMan;
	}
	
	public void submitApplication(ApplicationFactory factory){
		winMan.submitApplicationToMenu(factory);
	}
	
	/**
	 * May be overridden if one wants to catch the exceptions
	 */
	@Override
	public void run(){
		
		WindowManager winMan = getWindowManager();
		try {
			winMan.start();
		} catch (IOException e) { 
		}finally{
			winMan.stop();
		}
		
	}
}
