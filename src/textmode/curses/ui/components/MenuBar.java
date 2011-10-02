package textmode.curses.ui.components;

import java.util.Enumeration;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.WindowManager;
import textmode.curses.ui.event.ActionEvent;
import textmode.curses.ui.event.FinishedActionEvent;
import textmode.curses.ui.event.FinishedActionListener;
import textmode.curses.ui.event.TermKeyEvent;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;



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
			int right = mi.getPosition().getCol() + mi.getSize().getCols() + 1;
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

	@Override
	protected synchronized void redraw() {
		clear();
	}



}
