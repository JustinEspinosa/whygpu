package fun.useless.curses.ui.components;

import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.WindowManager;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;

public class PopUp extends AbstractMenu {

	private int maxCols = 30;
	
	public PopUp(int cols,WindowManager m) {
		super(0,1,m);
		maxCols = cols;
		setVisible(false);
	}
	
	public void addItem(MenuItem m){
		
		m.setPosition( new Position(getSize().getLines(), 0) );

		if(m.getText().length() <= maxCols && m.getText().length()>getSize().getCols() )
			setSize(new Dimension(getSize().getLines()+1, m.getText().length()));
		else
			setSize(getSize().down());
		
		m.setSize( new Dimension(1,getSize().getCols()) );
		
		intAddChild(m);
		if(selectedIndex==-1)
			selectedIndex = 0;
	}

	public void open(){
		setVisible(true);
		gotFocus();
	}
	
	public void close(){
		setVisible(false);
		lostFocus();
	}
	
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				if(k == TermKeyEvent.DOWN_ARROW){
					nextItem();
					return;
				}
				if(k == TermKeyEvent.UP_ARROW){
					previousItem();
					return;
				}
				if(k == TermKeyEvent.CANCEL){
					close();
					return;
				}
			}
		}
		
		super.processEvent(e);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(getSelectedItem()!=null)
			if(getSelectedItem().getTarget()==null)
				close();
			
		super.actionPerformed(e);
	}
}
