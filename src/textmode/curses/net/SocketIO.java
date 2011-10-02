package textmode.curses.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import textmode.curses.term.io.NoWaitIOLock;


public abstract class SocketIO {

	private SocketChannel socket;
	
	protected SocketIO(SocketChannel sock){
		socket = sock;
	}

	public void wakeup(){
		getIOLock().wakeup();
	}
	
	public abstract OutputStream getOutputStream() throws IOException;

	public abstract InputStream getInputStream() throws IOException;

	public abstract NoWaitIOLock getIOLock();

	public SocketIO() {
		super();
	}

	protected final SocketChannel getSocket() {
		return socket;
	}

}