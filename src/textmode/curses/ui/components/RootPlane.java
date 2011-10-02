package textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.application.Application;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.WindowManager;


public abstract class RootPlane<T extends Component> extends AbstractWindow<T> {

	private WindowManager manager;

	public RootPlane(WindowManager m,String title,Curses cs, Position p, Dimension d) {
		super(title,cs,p,d);
		manager = m;
	}
	
	public synchronized void activateList(Vector<T> list)
	{
		T focused = null;
		Vector<T> zOrderedList = new Vector<T>();
		
		Enumeration<T> children = children();
		
		
		while(children.hasMoreElements()){
			T child = children.nextElement();
			if(list.contains(child)){
				zOrderedList.add(child);
				focused = child;
			}
		}
		
		children = zOrderedList.elements();
		
		while(children.hasMoreElements())
			bringToFront(children.nextElement(),false);
		
		if(focused!=null)
			setFocus(focused);
	}
	
	protected void releaseMe(){
		manager.releaseFocus(this);
	}
	
	protected final WindowManager getWindowManager(){
		return manager;
	}
	
	public Application getCurrentApplication(){
		return manager.getCurrentApplication();
	}

	@Override
	protected boolean isActive() {
		return false;
	}

}
