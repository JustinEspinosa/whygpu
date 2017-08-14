package com.github.justinespinosa.textmode.curses.ui;

import com.github.justinespinosa.textmode.curses.ui.components.RootPlane;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;

public interface UiEventProcessorFactory {
	public UiEventProcessor createProcessor(UiEvent e,RootPlane<?> plane);
}
