package textmode.curses.ui.components;

import java.util.Enumeration;

import textmode.curses.Curses;
import textmode.curses.lang.ColorChar;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.WindowManager;
import textmode.curses.ui.event.FinishedActionEvent;
import textmode.curses.ui.event.FinishedActionListener;
import textmode.curses.ui.event.ResolutionChangeEvent;
import textmode.curses.ui.event.UiEvent;


public class MenuPlane extends RootPlane<AbstractMenu> implements FinishedActionListener{

	public MenuPlane(WindowManager m,Curses cs,Position p,Dimension d) {
		super(m,"_transp_",cs,p,d);
		//allows to draw children over transparent places
		transparent = true;
		setEventReceiver(getWindowManager());
	}
	
	@Override
	protected void createArray(ColorChar[][] arr) {
	}
	
	@Override
	protected void init(){}
	
	public void setFocus(AbstractMenu c){
		AbstractMenu f = getFocused();
		if(f!=null)
			f.removeFinishedActionListener(this);
		super.setFocus(c);
		c.addFinishedActionListener(this);
	}
	
	public void actionFinished(FinishedActionEvent e) {
		if(e.getSource() instanceof AbstractMenu){
			if(e.mustCloseParent()){
				releaseMe();
				((AbstractMenu)e.getSource()).removeFinishedActionListener(this);
			}
		}
	}
	
	@Override
	public void processEvent(UiEvent e){
		if(e instanceof ResolutionChangeEvent){
			Enumeration<AbstractMenu> en = children();
			while(en.hasMoreElements())
				en.nextElement().processEvent(e);
			
			return;
		}
		super.processEvent(e);
	}
	
	@Override
	protected synchronized void redraw() {
		
	}

}
