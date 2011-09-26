package fun.useless.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.data.ListBoxModel;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.SelectionChangeEvent;
import fun.useless.curses.ui.event.SelectionChangedListener;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;

public class ListBox extends ScrollPane<Panel<LBLabel> > {
	
	private ListBoxModel dataModel;
	private int line;
	private int width;
	private int selectedIndex = -1;
	private Vector<ActionListener> aListeners = new Vector<ActionListener>();
	private Vector<SelectionChangedListener> sListeners = new Vector<SelectionChangedListener>();

	public ListBox(ListBoxModel model,Curses cs,Position p, Dimension d) {
		super(new Panel<LBLabel>(cs,new Position(0,0),new Dimension(1,1)),cs, p, d);
		dataModel = model;
		line = 0;
		setColor(ColorDefaults.getDefaultColor(ColorType.BUTTON,curses()));
		getComponent().setColor(ColorDefaults.getDefaultColor(ColorType.BUTTON,curses()));
		clear();
		updateFromModel();
	}
	
	private void addItem(String text){
		getComponent().addChild(new LBLabel(text,curses(),new Position(line++,0),new Dimension(1,text.length())));
		if(text.length()> width)
			width = text.length();
	}
	
	private void reset(){
		deSelect();
		line = 0;
		width = 1;
		getComponent().removeChildren();
	}
	
	private void resize(){
		getComponent().setSize(new Dimension(line,width));
		notifyDisplayChange();
	}
	
	public ListBoxModel getModel(){
		return dataModel;
	}
	
	public void updateFromModel(){
		reset();
		
		
		int count = dataModel.getItemCount();
		for(int i=0;i<count;i++)
			addItem(dataModel.getItemAt(i).toString());

		
		
		reSelect();
		
		resize();
	}
	
	
	private void deSelect(){
		
		if(selectedIndex>=0 && selectedIndex<dataModel.getItemCount()){
			getComponent().getAtIndex(selectedIndex).deselect();
		}
		
	}
	
	private void reSelect(){
		
		if(selectedIndex>=dataModel.getItemCount()){
			selectedIndex = dataModel.getItemCount()-1;
		}
		
		if(selectedIndex<0 && dataModel.getItemCount()>0){
			selectedIndex = 0;
		}
		
		notifySeletionChanged(new SelectionChangeEvent(this));
		if(dataModel.getItemCount()>0){
			LBLabel  item = getComponent().getAtIndex(selectedIndex);
			item.select();
			scrollToView(item.getPosition());
		}
	}
	
	
	public void removeActionListener(ActionListener l){
		aListeners.remove(l);
	}
	
	public void addActionListener(ActionListener l){
		aListeners.add(l);
	}
	
	public int selectedIndex(){
		return selectedIndex;
	}
	
	public Object selectedItem(){
		if(selectedIndex>-1)
			return dataModel.getItemAt(selectedIndex);
		else
			return null;
	}
	
	protected final void notifyAction(ActionEvent e){
		Enumeration<ActionListener> eA = aListeners.elements();
		while(eA.hasMoreElements())
			eA.nextElement().actionPerformed(e);
	}
	
	
	public void removeSelectionListener(SelectionChangedListener l){
		sListeners.remove(l);
	}
	
	public void addSelectionListener(SelectionChangedListener l){
		sListeners.add(l);
	}
	
	protected final void notifySeletionChanged(SelectionChangeEvent e){
		Enumeration<SelectionChangedListener> eS = sListeners.elements();
		while(eS.hasMoreElements())
			eS.nextElement().selectionChanged(e);
	}
	
	@Override
	public void processEvent(UiEvent e) {

		if(e instanceof UiInputEvent && !isScrollLock()){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				int k = ((CharacterCodeEvent)uie.getOriginalEvent()).getChar();
				switch( k ){
				case 13:
					notifyAction(new ActionEvent(this));
					return;
				}
			}
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				switch( k ){
				case TermKeyEvent.LEFT_ARROW:
				case TermKeyEvent.UP_ARROW:
					deSelect();
					selectedIndex--;
					reSelect();
					return;
				case TermKeyEvent.RIGHT_ARROW:
				case TermKeyEvent.DOWN_ARROW:
					deSelect();
					selectedIndex++;
					reSelect();
					return;
				}
			}
		}
		super.processEvent(e);

	}

}
