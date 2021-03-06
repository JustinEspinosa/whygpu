package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.data.TableLayout;
import com.github.justinespinosa.textmode.curses.ui.data.TableModel;
import com.github.justinespinosa.textmode.curses.ui.event.ActionListener;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.SelectionChangedListener;


public class TableBox extends ScrollPane<Table> implements SelectionChangedListener{
	
	private Table table;

	public TableBox(TableLayout layout, TableModel model, Curses cs, Position p, Dimension d) {
		super(new Table(model,layout,cs,Position.ORIGIN,Dimension.UNITY),cs, p, d);
		table = (Table) getComponent();
		table.addSelectionListener(this);
	}

	
	public TableModel getModel(){
		return table.getModel();
	}
	
	public TableLayout getLayout(){
		return table.getLayout();
	}
	
	public void updateFromModel(){
		table.updateFromModel();
	}
	
	public void removeActionListener(ActionListener l){
		table.removeActionListener(l);
	}
	
	public void addActionListener(ActionListener l){
		table.addActionListener(l);
	}
	
	public int selectedIndex(){
		return table.selectedIndex();
	}
	
	public Object selectedItem(){
		return table.selectedItem();
	}
	
	public void removeSelectionListener(SelectionChangedListener l){
		table.removeSelectionListener(l);
	}
	
	public void addSelectionListener(SelectionChangedListener l){
		table.addSelectionListener(l);
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
