package shittytests;

import java.io.IOException;

import textmode.curses.Curses;
import textmode.curses.term.Terminal;
import textmode.curses.term.termcap.TermCap;





public class TestCurses {

	public static void main(String[] args){
		try{
			TermCap capdb = new TermCap("termcap.src");
			String trm=System.getenv("TERM");
			if(trm==null)
				trm = "ansi";
			Terminal term = new Terminal(capdb.getTermType(trm));
			
			
			Curses crs = new Curses(term);
			
			crs.clear();
			for(int j=0;j<8;j++){
				crs.bColor(j);
				for(int i=0;i<8;i++){
					crs.fColor(i);
					crs.printAt("Hello", i, j*6);
				}
			}
			term.getChar();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
