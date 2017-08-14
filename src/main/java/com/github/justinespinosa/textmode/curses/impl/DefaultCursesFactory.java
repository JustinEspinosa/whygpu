package com.github.justinespinosa.textmode.curses.impl;

import java.io.IOException;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.CursesFactory;
import com.github.justinespinosa.textmode.curses.impl.TermCapCurses;
import com.github.justinespinosa.textmode.curses.net.SocketIO;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.termcap.TermCap;
import com.github.justinespinosa.textmode.curses.term.termcap.TermType;

public class DefaultCursesFactory extends CursesFactory {
	
	private TermCap termcap;
	
	public DefaultCursesFactory(String termCapFile) throws IOException{
		termcap = new TermCap(termCapFile);
	}

	public DefaultCursesFactory() throws IOException{
		termcap = new TermCap();
	}

	public Curses createCurses(String ttname) throws IOException{
		return new TermCapCurses(createTerminal(ttname));
	}

	public Curses createCurses(Terminal t){
		return new TermCapCurses(t);
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
