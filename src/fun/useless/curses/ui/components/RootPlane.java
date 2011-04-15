package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.ui.WindowManager;

public class RootPlane<T extends Component> extends AbstractWindow<T> {

	private WindowManager manager;

	public RootPlane(WindowManager m,String title, int sLine, int sCol, int lines, int cols) {
		super(title, sLine, sCol, lines, cols);
		manager = m;
	}
	
	public void activateList(Vector<T> list)
	{
		T lastChild = null;
		Enumeration<T> children = children();
		while(children.hasMoreElements())
		{
			T child = children.nextElement();
			if(list.contains(child)){
				bringToFront(child,false);
				lastChild = child;
			}
		}
		if(lastChild!=null)
			setFocus(lastChild);
	}
	
	protected void releaseMe(){
		manager.releaseFocus(this);
	}
	
	protected final WindowManager getWindowManager(){
		return manager;
	}
	

}
