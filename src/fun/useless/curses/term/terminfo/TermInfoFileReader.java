package fun.useless.curses.term.terminfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class TermInfoFileReader extends BufferedReader {
	
	public TermInfoFileReader(Reader r){
		super(r);
	}

	private boolean isComment(String line){
		if(line==null) return false;
		return (line.length()>0 && line.charAt(0)=='#');
	}
	
	private String readLineSkipComments() throws IOException {
		String line=super.readLine();
		while( isComment(line)){
			line=super.readLine();
		}
		return line;
	}
	
	@Override
	public String readLine() throws IOException {
		return readLineSkipComments();
	}
}
