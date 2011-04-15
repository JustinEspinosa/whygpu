package shittytests;

import java.io.IOException;

import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.termcap.TermCap;




public class TestClear {

	public static void main(String[] args){
		try{
			TermCap capdb = new TermCap("termcap");
			String trm=System.getenv("TERM");
			if(trm==null)
				trm = "ansi";
			Terminal term = new Terminal(capdb.getTermType(trm));
			
			term.writeCommand("cl", 24);
			
			term.getChar();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
