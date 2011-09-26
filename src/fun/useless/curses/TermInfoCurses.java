package fun.useless.curses;

import java.io.IOException;

import fun.useless.curses.term.Terminal;

public class TermInfoCurses extends Curses {

	public TermInfoCurses(Terminal t) {
		super(t);
	}

	private Terminal term(){
		return getTerminal();
	}
	
	@Override
	public void initColor() throws IOException{
		term().writeCommand("oc", 0);
	}
	
	@Override
	public void setStandout(boolean so) throws IOException{
		if(so)
			term().writeCommand("smso", 0);
		else
			term().writeCommand("rmso", 0);
	}
	
	@Override
	public void high() throws IOException{
		term().writeCommand("md", 0);
	}
	
	@Override
	public void low() throws IOException{
		term().writeCommand("mh", 0);
	}
	
	@Override
	public void bColor(int c) throws IOException{
		if(c>-1){
			if(term().hasCommand("setb"))
				term().writeCommand("setb", 0, c);
			else
				term().writeCommand("setab", 0, c);
		}
	}
	
	@Override
	public void fColor(int c) throws IOException{
		if(c>-1){
			if(term().hasCommand("setf"))
				term().writeCommand("setf", 0, c);
			else
				term().writeCommand("setaf", 0, c);
		}
	}
	
	@Override
	public void civis() throws IOException{
		term().writeCommand("civis",0);
	}
	
	@Override
	public void cnorm() throws IOException{
		term().writeCommand("cnorm",0);
	}
	
	@Override
	public void rmcup() throws IOException{
		term().writeCommand("rmcup",term().getLines());
	}

	@Override
	public void smcup() throws IOException{
		term().writeCommand("smcup",term().getLines());
	}
	
	@Override
	public void clear() throws IOException{
		term().writeCommand("clear", lines());
	}
	
	@Override
	protected void implCursorAt(int line,int col) throws IOException{
		term().writeCommand("cup", 1, line , col);
	}
	
	//TODO: printInStatus
}
