package com.github.justinespinosa.textmode.curses;

public interface TerminalResizedReceiver {
	public void terminalResized(int cols,int lines);
}
