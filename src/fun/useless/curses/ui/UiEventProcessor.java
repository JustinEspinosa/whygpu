package fun.useless.curses.ui;

import fun.useless.curses.ui.components.RootPlane;
import fun.useless.curses.ui.event.UiEvent;

public class UiEventProcessor extends Thread{
	private UiEvent ev;
	private RootPlane<?> eventPlane;

	public UiEventProcessor(UiEvent e,RootPlane<?> plane){
		ev = e;
		eventPlane = plane;
	}
	@Override
	public void run() {
		eventPlane.processEvent(ev);
	}
}