package textmode.curses.ui;

import textmode.curses.ui.components.RootPlane;
import textmode.curses.ui.event.UiEvent;

public interface UiEventProcessorFactory {
	public UiEventProcessor createProcessor(UiEvent e,RootPlane<?> plane);
}
