package fun.useless.curses.ui.components;

import java.util.Enumeration;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.FinishedActionEvent;
import fun.useless.curses.ui.event.FinishedActionListener;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;


public class MenuBar extends AbstractMenu implements FinishedActionListener{

	/**
	 * 
	 */
	private int selectedIndex = -1;
	
	public MenuBar(Curses cs,WindowManager m) {
		super(cs,new Dimension(1,m.getWidth()),m);
		clear();
	}
	
	public synchronized void addPopUp(String title,PopUp menu){
		int offset = 0;
		Enumeration<MenuItem> ec = children();
		while(ec.hasMoreElements()){
			MenuItem mi = ec.nextElement();
			int right = mi.getPosition().getCol() + mi.getSize().getCols();
			offset = Math.max(offset, right);
		}
		
		MenuItem m = new MenuItem(title,curses());
		m.setPosition( new Position(0, offset) );
		m.setTarget(menu);
		m.setTargetPosition(1, offset);
		intAddChild(m);
		
		menu.addFinishedActionListener(this);
		
		if(selectedIndex==-1)
			selectedIndex = 0;
	}
	
	@Override
	public void processEvent(UiEvent e) {
		
		if(getSelectedItem()!=null && getSelectedItem().getTarget()!=null && getSelectedItem().getTarget().isVisible()){
			getSelectedItem().getTarget().processEvent(e);
			return;
		}
		
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				if(k == TermKeyEvent.RIGHT_ARROW){
					nextItem();
					return;
				}
				if(k == TermKeyEvent.LEFT_ARROW){
					previousItem();
					return;
				}
				if(k == TermKeyEvent.CANCEL){
					notifyFinish(true);
					return;
				}
			}
		}
		
		super.processEvent(e);
	}

	public void actionFinished(FinishedActionEvent e) {
		if(e.mustCloseParent())
			notifyFinish(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof MenuItem){
			MenuItem m = (MenuItem)e.getSource();
			if(hasChild(m)){
				m.deployTarget();
			}
		}
	}



}
