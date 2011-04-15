package fun.useless.curses.ui.components;

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
	
	public Window(String title,Application app,Position position,Dimension dimension){
		this(title,app,position.getLine(),position.getCol(),dimension.getLines(),dimension.getCols());
	}
	public Window(String title,Application app,int sLine,int sCol,int lines,int cols){
		super(title,sLine,sCol,lines,cols);

		application = app;
		
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW));
		setBorder(true);
		clear();
	}
	
	public final Application getOwner(){
		return application;
	}
	
	public final void close(){
		sendEvent(new CloseMe(this));
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

}
