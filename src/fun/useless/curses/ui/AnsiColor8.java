package fun.useless.curses.ui;

public enum AnsiColor8 implements Color {
	Black (0),
	Red (1),
	Green (2),
	Yellow (3),
	Blue (4),
	Magenta (5),
	Cyan (6),
	White (7);
	private int colIndex;
	private AnsiColor8(int n){
		colIndex = n;
	}
	public int index(){
		return colIndex;
	}
}
