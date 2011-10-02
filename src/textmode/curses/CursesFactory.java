package textmode.curses;

import java.io.IOException;

import textmode.curses.net.SocketIO;
import textmode.curses.term.Terminal;
import textmode.curses.term.termcap.TermType;

public interface CursesFactory {
	
	public Curses createCurses(Terminal t);
	public TermType createTermType(String ttname);
	public Terminal createTerminal(String ttname) throws IOException;
	public Terminal createTerminal(TermType tt) throws IOException;
	public Terminal createTerminal(String ttname, SocketIO io) throws IOException;
	public Terminal createTerminal(TermType tt, SocketIO io) throws IOException;

}
