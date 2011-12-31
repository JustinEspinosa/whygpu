package textmode.curses.ui.components;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.data.ListModel;
import textmode.curses.ui.data.TableLayout;
import textmode.curses.ui.data.TableModel;
import textmode.curses.ui.event.ActionListener;
import textmode.curses.ui.event.SelectionChangedListener;

public class Table extends Panel<List> {

	private class ListModelFromTableModel implements ListModel{
		private int tableColumn;
		private ListModelFromTableModel(int column){
			tableColumn = column;
		}
		public int getItemCount() {
			return tableModel.getLineCount();
		}
		public Object getItemAt(int index) {
			return tableModel.getItemAt(index, tableColumn);
		}
	}
	
	private List masterList = null;
	private TableModel  tableModel;
	private TableLayout tableLayout;
	
	public Table(TableModel model, TableLayout layout, Curses cs, Position p, Dimension d) {
		super(cs, p, d);
		tableModel  = model;
		tableLayout = layout;
		initializeTable();
	}
	
	private Dimension columnSize(int col){
		return new Dimension(1,tableLayout.getColumnWidth(col));
	}
	
	private Position columnPosition(int col){
		int position=0;
		
		for(int i=0;i<col;++i)
			position += tableLayout.getColumnWidth(i);
		
		return new Position(0,position);
	}
	
	private void resize(){
		setSize(new Dimension(tableModel.getLineCount(), columnPosition(tableModel.getColumnCount()).getCol()));
	}
	
	private void initializeTable() {
		masterList = new List(new ListModelFromTableModel(0), curses(), columnPosition(0), columnSize(0));
		masterList.setMaxWidth(tableLayout.getColumnWidth(0));
		addChild(masterList);
		
		for(int i=1;i<tableModel.getColumnCount();++i){
			List list = new List(new ListModelFromTableModel(i), curses(), columnPosition(i), columnSize(i),masterList);
			list.setMaxWidth(tableLayout.getColumnWidth(i));
			addChild(list);
		}
		resize();
	}
	
	
	public TableModel getModel(){
		return tableModel;
	}

	public TableLayout getLayout() {
		return tableLayout;
	}
	
	public void updateFromModel(){
		masterList.updateFromModel();
		resize();
	}
	
	public void removeActionListener(ActionListener l){
		masterList.removeActionListener(l);
	}
	
	public void addActionListener(ActionListener l){
		masterList.addActionListener(l);
	}
	
	public int selectedIndex(){
		return masterList.selectedIndex();
	}
	
	/**
	 * The object of the first column
	 * @return
	 */
	public Object selectedItem(){
		return masterList.selectedItem();
	}
	
	public void removeSelectionListener(SelectionChangedListener l){
		masterList.removeSelectionListener(l);
	}
	
	public void addSelectionListener(SelectionChangedListener l){
		masterList.addSelectionListener(l);
	}
	
}
