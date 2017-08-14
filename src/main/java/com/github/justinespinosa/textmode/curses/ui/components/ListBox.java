package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.data.ListModel;
import com.github.justinespinosa.textmode.curses.ui.event.ActionListener;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangedListener;


public class ListBox extends ScrollPane<List> implements SelectionChangedListener{
	
	private List list;

	public ListBox(ListModel model, Curses cs, Position p, Dimension d) {
		super(new List(model,cs,Position.ORIGIN,Dimension.UNITY),cs, p, d);
		list = getComponent();
		list.addSelectionListener(this);
	}

	
	public ListModel getModel(){
		return list.getModel();
	}
	
	public void updateFromModel(){
		list.updateFromModel();
	}
	
	public void removeActionListener(ActionListener l){
		list.removeActionListener(l);
	}
	
	public void addActionListener(ActionListener l){
		list.addActionListener(l);
	}
	
	public int selectedIndex(){
		return list.selectedIndex();
	}
	
	public Object selectedItem(){
		return list.selectedItem();
	}
	
	public void removeSelectionListener(SelectionChangedListener l){
		list.removeSelectionListener(l);
	}
	
	public void addSelectionListener(SelectionChangedListener l){
		list.addSelectionListener(l);
	}

	@Override
	protected synchronized void redraw() {
		clear();
	}


	public void selectionChanged(SelectionChangeEvent e) {
		if(e.getSelection()!=null)
			scrollToView(e.getSelection().getPosition());
	}

}
