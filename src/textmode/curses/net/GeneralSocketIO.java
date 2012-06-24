package textmode.curses.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import textmode.curses.term.io.ChannelInputStream;
import textmode.curses.term.io.ChannelOutputStream;
import textmode.curses.term.io.NoWaitIOLock;


public class GeneralSocketIO extends SocketIO {
	
	private NoWaitIOLock        nwLock;
	private ChannelInputStream  is;
	private ChannelOutputStream os;

	
	public GeneralSocketIO(SocketChannel sock) throws IOException{
		super(sock);
		nwLock = new NoWaitIOLock(getSocket());
		is = new ChannelInputStream(nwLock);
		os = new ChannelOutputStream(nwLock);
	}
	
	@Override
	public NoWaitIOLock getIOLock() {
		return nwLock;
	}
	
	@Override
	public InputStream getInputStream() throws IOException{
		return is;
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException{
		return os;
	}
}
