package textmode.curses;

import java.io.IOException;

import textmode.curses.net.SocketIO;
import textmode.curses.term.Terminal;
import textmode.curses.term.termcap.TermCap;
import textmode.curses.term.termcap.TermType;

public class DefaultCursesFactory implements CursesFactory{
	
	private TermCap termcap;
	
	public DefaultCursesFactory(String termCapFile) throws IOException{
		termcap = new TermCap(termCapFile);
	}
	
	public Curses createCurses(Terminal t){
		return new Curses(t);
	}
	
	public TermType createTermType(String ttname){
		return termcap.getTermType(ttname);
	}
	
	public Terminal createTerminal(String ttname) throws IOException{
		return createTerminal(ttname,null);
	}

	public Terminal createTerminal(TermType tt) throws IOException{
		return createTerminal(tt,null);
	}	
	
	public Terminal createTerminal(String ttname, SocketIO io) throws IOException{
		return createTerminal(createTermType(ttname),io);
	}

	public Terminal createTerminal(TermType tt, SocketIO io) throws IOException{
		if(io!=null)
			return new Terminal(tt,io);
		
		return new Terminal(tt);
	}
}
