package fun.useless.curses;


import java.io.IOException;

import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.io.ConsoleInputStream;
import fun.useless.curses.ui.ColorChar;
import fun.useless.curses.ui.components.Component;



public class Curses {
	
	private Terminal term;
	private ConsoleInputStream is;	
	
	public Curses(Terminal t){
		term = t;
		
		//Allow curses to manage edition
		is = new ConsoleInputStream(term.getInputStream(),term.getOutputStream());
		term.replaceInputStream(is);
	}
	
	public Terminal getTerminal(){
		return term;
	}
	
	public void initColor() throws IOException{
		term.writeCommand("Ic", 0);
	}
	public void bColor(int c) throws IOException{
		if(c>-1)
			term.writeCommand("AB", 0, c);
	}	
	public void fColor(int c) throws IOException{
		if(c>-1)
			term.writeCommand("AF", 0, c);
	}
	public void civis() throws IOException{
		term.writeCommand("vi",0);
	}
	public void cnorm() throws IOException{
		term.writeCommand("ve",0);
	}
	
	public void noecho() throws IOException{
		is.setEcho(false);
	}
	public void echo() throws IOException{
		is.setEcho(true);
	}
	
	public void rmcup() throws IOException{
		term.writeCommand("te",term.getLines());
	}
	public void smcup() throws IOException{
		term.writeCommand("ti",term.getLines());
		
	}
	public int colorCount() throws IOException{
		return term.getColorCount();
	}
	
	public void noraw() throws IOException{
		is.setCanonical(true);
	}
	public void raw() throws IOException{
		is.setCanonical(false);
	}
	
	public void showWindow(Component w) throws IOException{
		drawColorCharArray(w.getContent(),w.getPosition().getLine(),w.getPosition().getCol());
	}
	
	//fill a text rectangle of defined characters + fore|back color as fast as possible
	public void drawColorCharArray(ColorChar[][] contents, int sLine,int sCol) throws IOException{
		StringBuilder bld = new StringBuilder();
		
		int startCol = 0;
		int color = -1;
		int backColor = -1;
		
		for(int line=0;line<contents.length;line++){
			for(int col=0;col<contents[line].length;col++){
				//null means nothing to for this char
				if(contents[line][col]!=null){
					//buffer while it is same color
					if(contents[line][col].getColor() == color && contents[line][col].getBackColor() == backColor){
						bld.append(contents[line][col].getChr());
					}else{
						bColor(backColor);
						fColor(color);
						printAt(bld.toString(),line+sLine,startCol+sCol);
						bld = new StringBuilder();
						backColor = contents[line][col].getBackColor();
						color = contents[line][col].getColor();
						startCol = col;
						bld.append(contents[line][col].getChr());
					}
				}
			}
			//finish displayings
			if(bld.length()>0){
				bColor(backColor);
				fColor(color);
				printAt(bld.toString(),line+sLine,startCol+sCol);
			}
			//reset
			bld = new StringBuilder();
			backColor = -1;
			color = -1;
			startCol = 0;
		}
		
	}
	
	public int lines() throws IOException{
		return term.getLines();
	}
	
	public int cols() throws IOException{
		return term.getCols();
	}
	
	public void clear() throws IOException{
		term.writeCommand("cl", lines());
	}
	
	public void printInStatus(String text) throws IOException{
		term.writeCommand("ts", 1);
		for(int i=0;i<text.length();i++)
			term.writeChar(text.charAt(i));
		term.writeCommand("fs", 1);
	}
	
		
	public void cursorAt(int line,int col) throws IOException{
		term.writeCommand("cm", 1, line , col);
	}
	public void printAt(String text,int l,int c) throws IOException{
		cursorAt(l,c);
		for(int i=0;i<text.length();i++)
			term.writeChar(text.charAt(i));
	}
}
