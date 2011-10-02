package textmode.curses;

import java.io.IOException;

import textmode.curses.net.SocketIO;
import textmode.curses.term.TermInfoTerminal;
import textmode.curses.term.Terminal;
import textmode.curses.term.termcap.TermType;
import textmode.curses.term.terminfo.TermInfo;


public class TermInfoCursesFactory implements CursesFactory {
	private TermInfo terminfo;
	
	public TermInfoCursesFactory(String termInfoFile) throws IOException{
		terminfo = new TermInfo(termInfoFile);
	}
	
	public Curses createCurses(Terminal t){
		return new TermInfoCurses(t);
	}
	
	public TermType createTermType(String ttname){
		return terminfo.getTermType(ttname);
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
			return new TermInfoTerminal(tt,io);
		
		return new TermInfoTerminal(tt);
	}
}
