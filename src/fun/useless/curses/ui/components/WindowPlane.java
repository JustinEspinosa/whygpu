package fun.useless.curses.ui.components;

import java.util.Enumeration;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.lang.ColorChar;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.Rectangle;
import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.CloseMe;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;

public class WindowPlane extends RootPlane<Window> {

	private Window  currentWindow=null;
	private static final int NORMAL = 0;
	private static final int MOVE = 1;
	private static final int RESIZE = 2;
	
	private int mode = NORMAL;
	
	private int cascwinCol  = 0;
	private int cascwinLine = 0;
	private Application currentApplication;
	
	public WindowPlane(WindowManager m, Curses cs,Position p,Dimension d) {
		super(m, "root", cs,p,d);
		
		setEventReceiver(getWindowManager());
		
		setColor(ColorDefaults.getDefaultColor(ColorType.DESKTOP,curses()));
		clear();
		printStatusNoUpdate();
	}

	
	/* MODE MANAGINGS */
	private void endMode(){
		if( mode != NORMAL){
			mode = NORMAL;
			if(currentWindow!=null) currentWindow.gotFocus();
			printStatus();
		}
	}
	
	private void startMode(int m){
		if(mode==m){
			endMode();
		}else{
			mode = m;
			if(currentWindow!=null) currentWindow.lostFocus();
			printStatus();
		}
	
	}
	
	private boolean scrollLock(){
		if(currentWindow != null)
			return currentWindow.isScrollLock();
		return false;
	}
	private Rectangle printStatusNoUpdate(){
		ColorPair cp = getColor();
		setColor(ColorDefaults.getDefaultColor(ColorType.MENU,curses()));
		String mv = (mode == MOVE  ) ? "@" : " ";
		String rs = (mode == RESIZE) ? "@" : " ";
		String sc = (scrollLock()  ) ? "@" : " ";
		String name = formatName("");
		if(currentApplication!=null)
			name = formatName(currentApplication.getName(true));
		
		String status = name+" |Press ESC-m for menu.  |Mv "+mv+" |Rs "+rs+" |Sc "+sc+" |";
		printAt(getInnerBottom(), 0, status);
		setColor(cp);
		return new Rectangle(getInnerBottom(),0,1,status.length());
	}
	
	private void printStatus(){
		notifyDisplayChange(printStatusNoUpdate());	
	}
	
	public void refreshStatus(){
		printStatusNoUpdate();	
	}
	
	private String generateSpaces(int num){
		StringBuilder strbld = new StringBuilder(num);
		for(int n=0;n<num;n++) strbld.append(' ');
		return strbld.toString();
	}
	
	private String formatName(String name){
		if(name.length()>=35)
			return name.substring(0,35);
		else
			return name.concat(generateSpaces(35 - name.length()));
	}
	/* CHILD WINDOWS MANAGINGS  yeah i know ;) */
	
	public Position getNextWindowPostion(){
		return new Position(nextCascwinLine(), nextCascwinCol());
	}
	
	private int nextCascwinCol(){
		cascwinCol++;
		if(cascwinCol > getSize().getCols()-20) cascwinCol = 1;
		return cascwinCol;
	}

	private int nextCascwinLine(){
		cascwinLine++;
		if(cascwinLine > getSize().getLines()-10) cascwinLine = 1;	
		return cascwinLine;
	}
	
	private void refreshWinArea(Window w){
		notifyDisplayChange(new Rectangle(w.getPosition(), w.getSize()));
	}
	
	private void closeWindow(Window w){
		if(w!=null){
			w.getOwner().hideWindow(w);
			previousWindow();
			refreshWinArea(w);
			if(currentWindow==w)
				currentWindow=null;
		}
	}
	
	private void closeWindow(){
		closeWindow(currentWindow);
	}
	
	private void setCurrentWindow(Window w){
		endMode();

		if(w.getOwner()==currentApplication)
			currentWindow = w;
		else
			currentWindow = null;
	}
	
	public void informOfCurrentlyActiveApplication(Application app){
		if(currentApplication!=app && currentWindow!=null){
			currentWindow.lostFocus();
		}
		
		currentApplication = app;
	}
	
	private Window findWindow(int direction){
		int index = direction<0?getChildCount()-1:0;
		Window w = getAtIndex(index);
		
		index += direction;
		while(w.getOwner() != currentApplication && index!=(direction<0?0:getChildCount()-1)){
			w = getAtIndex(index);
			index += direction;
		}
		
		if(w.getOwner()==currentApplication)
			return w;
		else
			return currentWindow;
	}
	
	private void previousWindow(){
		if(getChildCount()>0 && currentWindow != null){
			bringToFront(findWindow(-1));
		}
	}
	
	private void nextWindow(){
		if(getChildCount()>0 && currentWindow != null){
			bringToFront(findWindow(1));
		}
	}
	
	private void startMove(){
		startMode(MOVE);
	}
	
	private void startResize(){
		startMode(RESIZE);
	}
	
	private void windowPosHrz(int d){
		if(currentWindow!=null)
			currentWindow.setPosition( currentWindow.getPosition().horizontal(d) );
	}
	private void windowPosVert(int d){
		if(currentWindow!=null)
			currentWindow.setPosition( currentWindow.getPosition().vertical(d) );
	}
	private void windowSizeHrz(int d){
		if(currentWindow!=null && currentWindow.isResizeable())
			currentWindow.setSize(currentWindow.getSize().horizontal(d) );
	}
	private void windowSizeVert(int d){
		if(currentWindow!=null && currentWindow.isResizeable())
			currentWindow.setSize(currentWindow.getSize().vertical(d));
	}
	
	public void addAndMakeActive(Window w){
		intAddChild(w);
		setFocus(w);
	}
	
	@Override
	protected void setFocus(Window c) {
		setCurrentWindow(c);
		if(currentWindow == c)
			super.setFocus(c);
	}
	
	@Override
	public ColorChar getCharAt(int line,int col){
		//The menu plane must be on top of us.
		ColorChar occ = getWindowManager().getTopCharAt(line, col);
		if(occ!=null) 
			return occ;
		else
			return super.getCharAt(line, col);
	}
	
	@Override
	public void processEvent(UiEvent e){
		if(e instanceof CloseMe){
			if(e.getSource() instanceof Window)
				closeWindow((Window)e.getSource());
			
			return;
		}
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				char c = ((CharacterCodeEvent)uie.getOriginalEvent()).getChar();
				if(c == 13 && mode!=NORMAL ){ endMode(); return; }
			}
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				
				switch(k){
				case TermKeyEvent.SCROLL:
					printStatus(); break;
				case TermKeyEvent.MOVE:
				    startMove();   return;
				case TermKeyEvent.RESIZE:
				    startResize(); return;
				case TermKeyEvent.CLOSE:
					closeWindow(); return; 
				case TermKeyEvent.NEXT:
				    nextWindow();  return;
				case TermKeyEvent.LEFT_ARROW:
					if(mode==MOVE)  { windowPosHrz(-1);   return; }
				    if(mode==RESIZE){ windowSizeHrz(-1);  return; }
				    break;
				case TermKeyEvent.RIGHT_ARROW:
					if(mode==MOVE)  { windowPosHrz(1);    return; }
				    if(mode==RESIZE){ windowSizeHrz(1);   return; }
				    break;
				case TermKeyEvent.UP_ARROW:
					if(mode==MOVE)  { windowPosVert(-1);  return; }
				    if(mode==RESIZE){ windowSizeVert(-1); return; }
				    break;
				case TermKeyEvent.DOWN_ARROW:
					if(mode==MOVE)  { windowPosVert(1);   return; }
				    if(mode==RESIZE){ windowSizeVert(1);  return; }
				    break;
				}
			}
		}
		super.processEvent(e);
		/* on scroll key, refresh after the event has been processed */
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				if(k== TermKeyEvent.SCROLL) printStatus();
			}
		}
	}
	
	public Window getTopMostWindow(Application owner){		
		Enumeration<Window> children = rchildren();
		
		while(children.hasMoreElements()){
			Window child = children.nextElement();
			if(child.getOwner()==owner)
				return child;
		}
		return null;
	}
	
	public void refresh(){
		notifyDisplayChange();
	}
	
	public Window getCurrentWindow(){
		return currentWindow;
	}
}
