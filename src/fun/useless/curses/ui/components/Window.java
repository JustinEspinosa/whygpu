package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.CloseMe;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;



public class Window extends AbstractWindow<Component>{
	
	private Application application;
	private boolean resizeable = true;
	
	public Window(String title,Application app,Curses cs,Position p,Dimension d){
		super(title,cs,p,d);
		application = app;
		
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW,curses()));
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


}
