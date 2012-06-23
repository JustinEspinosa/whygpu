package textmode.curses.term.io;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public class NoWaitIOLock {
	
	private static OperationLock readLock;
	private static OperationLock writeLock;
	
	private SelectableChannel channel;
	
	
	public NoWaitIOLock(SelectableChannel chan) throws IOException {
		channel = chan;
		channel.configureBlocking(false);
		
		readLock  = new OperationLock(channel, SelectionKey.OP_READ);
		writeLock = new OperationLock(channel, SelectionKey.OP_WRITE);

	}  
	
    
	public void wantRead() throws InterruptedException, IOException{
		readLock.wantDo();
	}
	
	public void doneRead(){
		readLock.done();
	}
	
	
	public void wantWrite() throws InterruptedException, IOException{
		writeLock.wantDo();
	}
	
	public void doneWrite(){
		writeLock.done();
	}
	
	
	public SelectableChannel channel(){
		return channel;
	}
	
	public void stopSelecting(){
		readLock.stopSelecting();
		writeLock.stopSelecting();
	}
	
	public void wakeup(){
		readLock.wakeup();
		writeLock.wakeup();
	}
	
	public synchronized void start(){
		readLock.start();
		writeLock.start();
	}
}
