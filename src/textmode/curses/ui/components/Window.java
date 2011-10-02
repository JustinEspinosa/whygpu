package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.event.CharacterCodeEvent;
import textmode.curses.ui.event.CloseMe;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;



public class Window extends AbstractWindow<Component>{
	
	private Application application;
	private boolean resizeable = true;
	
	public Window(String title,Application app,Curses cs,Position p,Dimension d){
		super(title,cs,p,d);
		application = app;
		setBorder(true);
		clear();
	}
	
	public final Application getOwner(){
		return application;
	}
	
	private void doClose(){
		sendEvent(new CloseMe(this));
	}
	
	public void close(){
		doClose();
	}
	
	public void setResizeable(boolean s){
		resizeable = s;
	}
	
	public boolean isResizeable(){
		return resizeable;
	}
	
	public boolean isScrollLock(){
		if(getFocused() instanceof ScrollPane<?>){
			return ((ScrollPane<?>)getFocused()).isScrollLock();
		}
		return false;
	}
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				int c = ((CharacterCodeEvent)uie.getOriginalEvent()).getChar();
				if(c == 9){ cycleFocus(); return; }
			}
		}
		super.processEvent(e);
	}
	
	@Override
	public void gotFocus() {
		if(hasBorder())
			border();
		super.gotFocus();
	}
	
	@Override
	public void lostFocus() {
		if(hasBorder())
			border();
		super.lostFocus();
	} 
	
	
	@Override
	protected boolean isActive() {
		return ( getOwner().getWindowManager().getWindowPlane().getCurrentWindow() == this &&
				 getOwner().getWindowManager().getCurrentApplication() == getOwner() );
	}

	@Override
	protected synchronized void redraw() {
		clear();
	}


}
