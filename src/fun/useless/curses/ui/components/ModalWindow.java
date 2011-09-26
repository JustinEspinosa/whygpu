package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

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
