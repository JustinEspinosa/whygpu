package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;

public class ModalWindow extends Window {

	private class WaitObject{}
	
	private WaitObject myWait = new WaitObject();
	
	public ModalWindow(String title, Application app, Curses cs,Position position,Dimension dimension) {
		super(title, app, cs,position, dimension);
		setResizeable(false);
	}
	
	protected final void modalFinish(){
	    synchronized(myWait){
	    	myWait.notify();
	    }
	}
	
	public void modalWait() throws InterruptedException{
		if(isActive()){
		    synchronized(myWait){
		    	myWait.wait();
			}
		}
	}
	
	@Override
	public void close() {
		super.close();
		modalFinish();
	}
	
}
