package fun.useless.curses.ui;

public enum AnsiColor16 implements Color {
	Black (0),
	Red (1),
	Green (2),
	Yellow (3),
	Blue (4),
	Magenta (5),
	Cyan (6),
	Gray (7),
	DarkGray (8),
	LightRed (9),
	LightGreen (10),
	LightYellow (11),
	LightBlue (12),
	LightMagenta (13),
	LightCyan (14),
	White (15);

	private int colIndex;
	private AnsiColor16(int n){
		colIndex = n;
	}
	public int index(){
		return colIndex;
	}
}
