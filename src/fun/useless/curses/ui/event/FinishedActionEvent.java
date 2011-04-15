package fun.useless.curses.ui.event;

public class FinishedActionEvent extends UiEvent {

	private boolean parentAlso;
	public FinishedActionEvent(Object src, boolean closesParent) {
		super(src);
		parentAlso = closesParent;
	}
	public boolean mustCloseParent(){
		return parentAlso;
	}

}
