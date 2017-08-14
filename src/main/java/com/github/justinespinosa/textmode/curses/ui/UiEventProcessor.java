package com.github.justinespinosa.textmode.curses.ui;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.ui.components.RootPlane;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;

public class UiEventProcessor extends Thread{
	private static long EvNr = 0;
	private UiEvent ev;
	private RootPlane<?> eventPlane;

	
	public UiEventProcessor(UiEvent e,RootPlane<?> plane){
		super(Thread.currentThread().getThreadGroup(),"Event-"+(++EvNr));
		ev = e;
		eventPlane = plane;
	}
	@Override
	public void run() {
		eventPlane.processEvent(ev);
	}
	
	protected final Application app(){
		return eventPlane.getCurrentApplication();
	}
	
	protected final Curses curses(){
		return eventPlane.curses();
	}
	
}