package shittytests;

import java.io.IOException;

import textmode.curses.term.Terminal;
import textmode.curses.term.termcap.TermCap;





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
