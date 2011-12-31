package textmode.curses.ui.event;

import textmode.curses.ui.components.Component;

public class SelectionChangeEvent extends UiEvent {

	private Component selection;
	
	public SelectionChangeEvent(Object src, Component selectedComponent) {
		super(src);
		selection = selectedComponent;
	}

	public Component getSelection(){
		return selection;
	}
}
