package textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.WindowManager;
import textmode.curses.ui.event.ActionEvent;
import textmode.curses.ui.event.ActionListener;
import textmode.curses.ui.event.FinishedActionEvent;
import textmode.curses.ui.event.FinishedActionListener;




public abstract class AbstractMenu extends MutableContainer<MenuItem> implements ActionListener{
	protected WindowManager manager;
	protected int selectedIndex = -1;
	protected Vector<FinishedActionListener> fLinisteners = new Vector<FinishedActionListener>();
	
	public AbstractMenu(Curses cs,Dimension d,WindowManager m) {
		super(cs,new Position(0,0),d);
		manager = m;
	}
	
	protected final void notifyFinish(boolean parent){
		FinishedActionEvent e = new FinishedActionEvent(this,parent);
		Enumeration<FinishedActionListener> fEnum = fLinisteners.elements();
		
		while(fEnum.hasMoreElements()) fEnum.nextElement().actionFinished(e);
	}
	
	public void addFinishedActionListener(FinishedActionListener l){
		fLinisteners.add(l);
	}
	
	public void removeFinishedActionListener(FinishedActionListener l){
		fLinisteners.remove(l);
	}
	
	public MenuItem getSelectedItem(){
		MenuItem c = null;
		
		if(getChildCount()==0)
			return null;
			
		if(selectedIndex<0 || selectedIndex>=getChildCount())
			nextItem();
		
		if(selectedIndex>=0 && selectedIndex<getChildCount())
			c = getAtIndex(selectedIndex);
		
		return c;
	}
	
	@Override
	public void gotFocus(){
		if(getSelectedItem()!=null)
			setFocus(getSelectedItem());
		super.gotFocus();
	}
	
	@Override
	public void lostFocus() {
		if(getSelectedItem()!=null)
			getSelectedItem().lostFocus();
		super.lostFocus();
	}
	protected void previousItem(){
		selectedIndex--;
		
		if(selectedIndex<0) selectedIndex = getChildCount()-1;
		
		if(getSelectedItem()!=null) setFocus(getSelectedItem());
	}	
	protected void nextItem(){
		selectedIndex++;
		
		if(selectedIndex>getChildCount()) selectedIndex = 0;
		
		if(getSelectedItem()!=null) setFocus(getSelectedItem());
	}
	
	@Override
	public void intAddChild(MenuItem m) {
		super.intAddChild(m);
		m.insertFirstActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof MenuItem){
			notifyFinish(true);
		}
	}
	
	
}
