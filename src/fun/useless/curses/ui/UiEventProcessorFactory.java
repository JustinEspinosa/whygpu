package fun.useless.curses.ui;

import fun.useless.curses.ui.components.RootPlane;
import fun.useless.curses.ui.event.UiEvent;

public interface UiEventProcessorFactory {
	public UiEventProcessor createProcessor(UiEvent e,RootPlane<?> plane);
}
