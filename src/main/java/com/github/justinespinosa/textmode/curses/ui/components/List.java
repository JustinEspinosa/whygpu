package com.github.justinespinosa.textmode.curses.ui.components;

import java.util.Enumeration;
import java.util.Vector;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.data.ListModel;
import com.github.justinespinosa.textmode.curses.ui.event.ActionEvent;
import com.github.justinespinosa.textmode.curses.ui.event.ActionListener;
import com.github.justinespinosa.textmode.curses.ui.event.CharacterCodeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangedListener;
import com.github.justinespinosa.textmode.curses.ui.event.TermKeyEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiInputEvent;

public class List extends Panel<LBLabel> {

	private ListModel dataModel;
	private int line;
	private int width;
	private int maxWidth = -1;
	private int selectedIndex = -1;
	private boolean slave = false;
	private Vector<ActionListener> aListeners = new Vector<ActionListener>();
	private Vector<SelectionChangedListener> sListeners = new Vector<SelectionChangedListener>();
	private Vector<List> slaves = new Vector<List>();
	
	public List(ListModel model, Curses cs, Position p, Dimension d, List master) {
		super(cs, p, d);
		if(master!=null)
			master.assignSlave(this);
		
		dataModel = model;
		line = 0;
		clear();
		updateFromModel();
	}
	
	public List(ListModel model, Curses cs, Position p, Dimension d) {
		this(model,cs, p, d, null);
	}
	
	
	private void setSlave(boolean isSlave){
		slave = isSlave;
	}
	
	private boolean isSlave() {
		return slave;
	}
	
	private void assignSlave(List slaveList){
		slaveList.setSlave(true);
		slaves.add(slaveList);
	}
	
	private void resizeItems(){
		Enumeration<LBLabel> e = children();
		while(e.hasMoreElements())
			e.nextElement().setSize(new Dimension(1,width));
			
	}
	
	private void addItem(String text){
		
		if(isSlave())
			text = "| "+text;
		
		LBLabel item = new LBLabel(text,curses(),new Position(line++,0),new Dimension(1,width));
		item.setMinSize(new Dimension(1, 4));
		if(maxWidth>=0)
			item.setMaxSize(new Dimension(1,maxWidth));
		item.refresh();
		if(item.getText().length()> width)
			width = item.getText().length();
		
		addChild(item);
	}
	
	public void setMaxWidth(int width) {
		maxWidth = width;
		Enumeration<LBLabel> e = children();
		while(e.hasMoreElements())
			e.nextElement().setMaxSize(new Dimension(1,maxWidth));
		
		resizeItems();
	}
	
	private void reset(){
		deSelect();
		line = 0;
		width = getSize().getCols();
		removeChildren();
	}
	
	private void resize(){
		if(maxWidth>=0 && width>maxWidth)
			width=maxWidth;
		setSize(new Dimension(line,width));
		resizeItems();
		notifyDisplayChange();
	}
	
	public ListModel getModel(){
		return dataModel;
	}
	
	public void updateFromModel(){
		updateFromModel(false);
	}
	
	public void updateFromModel(boolean keepSelection){
		reset();
		
		if(!keepSelection)
			selectedIndex = 0;
		
		int count = dataModel.getItemCount();
		for(int i=0;i<count;i++)
			addItem(dataModel.getItemAt(i).toString());

		Enumeration<List> el = slaves.elements();
		while(el.hasMoreElements())
			el.nextElement().updateFromModel();

		reSelect();
		resize();		
	}
	
	
	private void deSelect(){
		
		if(selectedIndex>=0 && selectedIndex<dataModel.getItemCount())
			getAtIndex(selectedIndex).deselect();
		
	}
	
	private void reSelect(){
		
		if(selectedIndex>=dataModel.getItemCount()){
			selectedIndex = dataModel.getItemCount()-1;
		}
		
		if(selectedIndex<0 && dataModel.getItemCount()>0){
			selectedIndex = 0;
		}
		
		if(dataModel.getItemCount()>0){
			LBLabel item = getAtIndex(selectedIndex);
			item.select();
			notifySelectionChanged(new SelectionChangeEvent(this,item));
		}else{
			selectedIndex = -1;
			notifySelectionChanged(new SelectionChangeEvent(this,null));
		}
	}
	
	
	public void removeActionListener(ActionListener l){
		aListeners.remove(l);
	}
	
	public void addActionListener(ActionListener l){
		aListeners.add(l);
	}
	
	public void selectIndex(int idx){
		deSelect();
		selectedIndex = idx;
		reSelect();
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
		if(isSlave())
			return;

		sListeners.remove(l);
	}
	
	public void addSelectionListener(SelectionChangedListener l){
		if(isSlave())
			return;

		sListeners.add(l);
	}
	
	protected final void notifySelectionChanged(SelectionChangeEvent e){
		Enumeration<List> el = slaves.elements();
		while(el.hasMoreElements())
			el.nextElement().selectIndex(selectedIndex);
		
		Enumeration<SelectionChangedListener> eS = sListeners.elements();
		while(eS.hasMoreElements())
			eS.nextElement().selectionChanged(e);
	}
	
	@Override
	public void processEvent(UiEvent e) {

		if(isSlave())
			return;
		
		if(e instanceof UiInputEvent){
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

	@Override
	protected synchronized void redraw() {
		clear();
	}
}
