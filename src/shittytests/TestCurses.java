package shittytests;

import java.io.IOException;

import fun.useless.curses.Curses;
import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.termcap.TermCap;




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
