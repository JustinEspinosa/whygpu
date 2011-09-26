package fun.useless.curses;


import java.io.IOException;

import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.io.ConsoleInputStream;
import fun.useless.curses.ui.BaseColor;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.lang.ColorChar;
import fun.useless.curses.lang.ColorString;
import fun.useless.curses.lang.ColorStringBuilder;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.components.Component;
import fun.useless.curses.ui.util.CharacterScreenBuffer;



public class Curses {
	
	private Terminal term;
	private ConsoleInputStream is;
	private CharacterScreenBuffer buffer;
	private int cursLine = -1;
	private int cursCol  = -1;
	
	public Curses(Terminal t){
		term = t;
		buffer = new CharacterScreenBuffer(new Dimension(t.getLines(), t.getCols()));
		//Allow curses to manage edition
		is = new ConsoleInputStream(term.getInputStream(),term.getOutputStream());
		term.replaceInputStream(is);

	}
	
	/*
	public void setUtf8(boolean c){
		is.setUtf8(c);
	}
	*/
	
	public Terminal getTerminal(){
		return term;
	}
	
	public void initColor() throws IOException{
		term.writeCommand("Ic", 0);
	}

	
	public int numcolors(){
		return term.getNumColors();
	}
	
	public void setStandout(boolean so) throws IOException{
		if(so)
			term.writeCommand("so", 0);
		else
			term.writeCommand("se", 0);
	}
	
	
	public void setIntensity(boolean high) throws IOException{
		if(high)
			high();
		else
			low();
	}
	
	public void high() throws IOException{
		term.writeCommand("md", 0);
	}
	
	public void low() throws IOException{
		term.writeCommand("mh", 0);
	}
	
	public void bColor(int c) throws IOException{
		if(c>-1){
			if(term.canAnsiColor())
				term.writeCommand("AB", 0, c);
			else
				term.writeCommand("Sb",0, c);
		}
	}	
	public void fColor(int c) throws IOException{
		if(c>-1){
			if(term.canAnsiColor())
				term.writeCommand("AF", 0, c);
			else
				term.writeCommand("Sf",0, c);

		}
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
		ColorStringBuilder bld = new ColorStringBuilder();

		for(int line=0;line<contents.length;line++){
			
			for(int col=0;col<contents[line].length;col++){
				bld.append(contents[line][col]);
			}
			
			doubleBufferPrintAt(bld.toColorString(),line + sLine,sCol);
			//reset
			bld = new ColorStringBuilder();
		}
		
	}
	
	public void applyColorPair(ColorPair color) throws IOException{
		
		
		
		/* Stand out inverts colors */
		bColor(color.getBackColor().index());
		fColor(color.getForeColor().index());
		
		if(term.getNumColors() == 1)
			setStandout(color.getBackColor().index() != 0);
		
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
	
	private void writeChar(char chr) throws IOException{
		term.writeChar(chr);
		if(cursCol>-1 && cursLine>-1){
			cursCol++;
			if(cursCol > term.getCols()){
				cursCol = 0;
				if(cursLine + 1 < term.getLines() )
					cursLine++;
			}
		}
	}
	
	public void printInStatus(String text) throws IOException{
		term.writeCommand("ts", 1);
		for(int i=0;i<text.length();i++)
			writeChar(text.charAt(i));
		term.writeCommand("fs", 1);
	}
	
	protected void implCursorAt(int line,int col) throws IOException{
		term.writeCommand("cm", 1, line , col);
	}
		
	public void cursorAt(int line,int col) throws IOException{
		if( cursLine!=line || cursCol!=col ){
			implCursorAt(line,col);
			cursLine = line;
			cursCol  = col;
		}
	}
	
	public void invalidateDoubleBuffering(){
		buffer.invalidate();
	}
	
	public void doubleBufferPrintAt(ColorString text,int line,int col) throws IOException{
		
		ColorPair lastColor   = new ColorPair(BaseColor.Undefined, BaseColor.Undefined);
		boolean   changeColor = false;
		
		for(int i=0;i<text.length();i++){
			ColorChar cchar = text.charAt(i);
			
			if(cchar!=null){
				changeColor = (!lastColor.equals(cchar.getColors()));
				
				Position p = new Position(line,col);
				if(buffer.set(p, cchar)){
					if(changeColor){
						applyColorPair(cchar.getColors());
						lastColor = cchar.getColors();
					}
					cursorAt(line, col);
					writeChar(cchar.getChr());
				}
			}
			col++;
		}
		
	}
	
	public void printAt(String text,int l,int c) throws IOException{
		cursorAt(l,c);
		byte[] chrs = text.getBytes();
		for(int i=0;i<chrs.length;i++)
			writeChar((char)chrs[i]);
	}
}
