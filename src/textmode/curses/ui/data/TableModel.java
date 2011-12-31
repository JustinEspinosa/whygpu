package textmode.curses.ui.data;

public interface TableModel {
	public int getColumnCount();
	public int getLineCount();
	public Object getItemAt(int line, int column);
}
