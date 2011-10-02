package textmode.curses.term.io;

import java.io.IOException;

public class InterruptIOException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 896925106754329661L;

	public InterruptIOException(){
		super();
	}
	public InterruptIOException(Exception cause){
		super(cause);
	}
}
