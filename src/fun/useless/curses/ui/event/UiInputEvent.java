package fun.useless.curses.ui.event;

public class UiInputEvent extends UiEvent {

	private TerminalInputEvent original;
	public UiInputEvent(TerminalInputEvent e) {
		super(e.getSource());
		original = e;
	}
	public TerminalInputEvent getOriginalEvent(){
		return original;
	}

}
