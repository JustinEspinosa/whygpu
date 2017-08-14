package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;

public class ModalWindow extends Window {

	private class WaitObject{}
	
	private WaitObject myWait = new WaitObject();
	
	public ModalWindow(String title, Application app, Curses cs, Position position, Dimension dimension) {
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
