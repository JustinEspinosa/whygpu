package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.event.FinishedActionEvent;
import fun.useless.curses.ui.event.FinishedActionListener;


public class MenuPlane extends RootPlane<AbstractMenu> implements FinishedActionListener{

	public MenuPlane(WindowManager m,Curses cs,Position p,Dimension d) {
		super(m,"_transp_",cs,p,d);
		//allows to draw children over transparent places
		transparent = true;
		setEventReceiver(getWindowManager());
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

}
