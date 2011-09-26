package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.WindowManager;

public class RootPlane<T extends Component> extends AbstractWindow<T> {

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

	@Override
	protected boolean isActive() {
		return false;
	}
	

}
