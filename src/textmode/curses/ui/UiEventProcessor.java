package textmode.curses.ui;

import textmode.curses.Curses;
import textmode.curses.application.Application;
import textmode.curses.ui.components.RootPlane;
import textmode.curses.ui.event.UiEvent;

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