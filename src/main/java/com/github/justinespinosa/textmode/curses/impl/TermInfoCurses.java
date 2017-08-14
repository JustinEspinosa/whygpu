package com.github.justinespinosa.textmode.curses.impl;

import java.io.IOException;

import com.github.justinespinosa.textmode.curses.AbstractCurses;
import com.github.justinespinosa.textmode.curses.term.Terminal;


class TermInfoCurses extends AbstractCurses {

	TermInfoCurses(Terminal terminal) {
		super(terminal);
	}


	@Override
	public void initColor() throws IOException{
		getTerminal().writeCommand("oc", 0);
	}

	@Override
	public void setStandout(boolean so) throws IOException{
		if(so)
			getTerminal().writeCommand("smso", 0);
		else
			getTerminal().writeCommand("rmso", 0);
	}

	@Override
	public void setIntensity(boolean high) throws IOException {

	}

	@Override
	public void high() throws IOException{
		getTerminal().writeCommand("md", 0);
	}
	
	@Override
	public void low() throws IOException{
		getTerminal().writeCommand("mh", 0);
	}
	
	@Override
	public void bColor(int c) throws IOException{
		if(c>-1){
			if(getTerminal().hasCommand("setb"))
				getTerminal().writeCommand("setb", 0, c);
			else
				getTerminal().writeCommand("setab", 0, c);
		}
	}
	
	@Override
	public void fColor(int c) throws IOException{
		if(c>-1){
			if(getTerminal().hasCommand("setf"))
				getTerminal().writeCommand("setf", 0, c);
			else
				getTerminal().writeCommand("setaf", 0, c);
		}
	}
	
	@Override
	public void civis() throws IOException{
		getTerminal().writeCommand("civis",0);
	}
	
	@Override
	public void cnorm() throws IOException{
		getTerminal().writeCommand("cnorm",0);
	}
	
	@Override
	public void rmcup() throws IOException{
		getTerminal().writeCommand("rmcup",getTerminal().getLines());
	}

	@Override
	public void smcup() throws IOException{
		getTerminal().writeCommand("smcup",getTerminal().getLines());
	}
	
	@Override
	public void clear() throws IOException{
		getTerminal().writeCommand("clear", lines());
	}

	@Override
	public void printInStatus(String text) throws IOException {
		getTerminal().writeCommand("tsl", 1);
		implPrint(text);
		getTerminal().flush();
		getTerminal().writeCommand("fsl", 1);
	}

	@Override
	protected void implCursorAt(int line,int col) throws IOException{
		getTerminal().writeCommand("cup", 1, line , col);
	}
	
}
