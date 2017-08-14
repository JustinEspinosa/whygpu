package com.github.justinespinosa.textmode.curses.impl;

import java.io.IOException;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.CursesFactory;
import com.github.justinespinosa.textmode.curses.impl.TermInfoCurses;
import com.github.justinespinosa.textmode.curses.net.SocketIO;
import com.github.justinespinosa.textmode.curses.term.TermInfoTerminal;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.term.termcap.TermType;
import com.github.justinespinosa.textmode.curses.term.terminfo.TermInfo;


public class TermInfoCursesFactory extends CursesFactory {
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
