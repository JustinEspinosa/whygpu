package fun.useless.curses.term.termcap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class TermCapFileReader extends BufferedReader {

	public TermCapFileReader(Reader r){
		super(r);
	}
	private boolean isComment(String line){
		if(line==null) return false;
		return (line.length()>0 && line.charAt(0)=='#');
	}
	private boolean lineContinues(String line){
		if(line==null) return false;
		return (line.length()>0 && line.charAt(line.length()-1)=='\\');
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
		String line = readLineSkipComments();
		
		while( lineContinues(line) )
		{
			line = line.substring(0,line.length()-1);
			String nextline = readLineSkipComments();
			line = line.concat(nextline.trim().substring(1));
		}
		
		return line;
	}
	

}
