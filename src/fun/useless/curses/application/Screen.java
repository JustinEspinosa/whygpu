package fun.useless.curses.application;

import java.io.IOException;

import fun.useless.curses.Curses;
import fun.useless.curses.term.Terminal;
import fun.useless.curses.ui.WindowManager;


public class Screen extends Thread{

	private Curses        curses;
	private WindowManager winMan;
	
	public Screen(Terminal t) throws IOException{
		curses   = new Curses(t);
		curses.noecho();
		curses.raw();
		curses.initColor();
		curses.civis();
		winMan = new WindowManager(curses);
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
		
		try {
			WindowManager winMan = getWindowManager();
			winMan.start();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		
	}
}
