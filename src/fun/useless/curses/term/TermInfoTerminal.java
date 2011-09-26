package fun.useless.curses.term;

import java.io.IOException;

import fun.useless.curses.net.SocketIO;
import fun.useless.curses.term.termcap.TermType;

public class TermInfoTerminal extends Terminal {

	public TermInfoTerminal(TermType t,SocketIO sock) throws IOException {
		super(t, sock);
	}

	public TermInfoTerminal(TermType t) {
		super(t);
	}

	@Override
	protected void getFlags() {
		try{
			setPC(type().getStr("pad"));
		}catch(NullPointerException e){
			setPC("0");
		}
		setNumColors(type().getNum("colors"));
		setCols(type().getNum("cols"));
		setLines(type().getNum("lines"));
	}
	
}
