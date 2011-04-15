package fun.useless.curses.ui.components;

import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.event.FinishedActionEvent;
import fun.useless.curses.ui.event.FinishedActionListener;


public class MenuPlane extends RootPlane<AbstractMenu> implements FinishedActionListener{

	public MenuPlane(WindowManager m,int sLine, int sCol, int lines, int cols) {
		super(m,"_transp_",sLine, sCol, lines, cols);
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
	
	@Override
	public void actionFinished(FinishedActionEvent e) {
		if(e.getSource() instanceof AbstractMenu){
			if(e.mustCloseParent()){
				releaseMe();
				((AbstractMenu)e.getSource()).removeFinishedActionListener(this);
			}
		}
	}

}
