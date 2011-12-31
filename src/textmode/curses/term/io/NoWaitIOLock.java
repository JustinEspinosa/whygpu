package textmode.curses.term.io;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NoWaitIOLock extends Thread {
	
	private static int ThId = 0;
	
	private SelectableChannel channel;
	private Selector selector;
	
	private Object readLock = new Object(){};
	private Object writeLock = new Object(){};
	
	private Map<Thread,InterruptIOException> rWaiters = new HashMap<Thread, InterruptIOException>();
	private boolean running = true;
	
	public NoWaitIOLock(SelectableChannel chan) throws IOException {
		super("IOLock-"+(++ThId));
		setDaemon(true);
		channel = chan;
		selector = channel.provider().openSelector();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}
	
    public void printStackTrace(StackTraceElement[] trace) {
       for (int i=0; i < trace.length; i++)
    	   System.out.println(trace[i]);
    }
	
	public  void readWait() throws InterruptIOException{
		Thread th = Thread.currentThread();
		
		//System.out.println(th+" Entered");
		//printStackTrace(th.getStackTrace());
		
		synchronized(rWaiters){
			rWaiters.put(th, null);
		}
		
		synchronized(readLock){
			try {
				readLock.wait();
			} catch (InterruptedException e) {
			}
		}
		
		InterruptIOException e;
		
		synchronized(rWaiters){
			e = rWaiters.get(th);
			rWaiters.remove(th);
		}
		
		//System.out.println(th+" Exited");
		//System.out.println(e);
		
		if(e!=null)
			throw new InterruptIOException(e);
	}
	
	public void writeWait(){
		SelectionKey key = null;
		try {
			key = channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);
		} catch (ClosedChannelException e1) {
			throw new RuntimeException(e1);
		}
		
		if(key!=null){
			synchronized(writeLock){
				try {
					writeLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public SelectableChannel channel(){
		return channel;
	}
	
	public synchronized void stopSelecting(){
		running = false;
		selector.wakeup();
		
		synchronized(readLock){
			readLock.notifyAll();
		}
		
		synchronized(writeLock){
			writeLock.notifyAll();
		}
	}
	
	public synchronized void wakeup(){
		
		synchronized(rWaiters){
			for(Thread th: rWaiters.keySet())
				rWaiters.put(th, new InterruptIOException());
		}
		
		synchronized(readLock){
			readLock.notifyAll();
		}
		
	}
	
	@Override
	public void run() {
		while(!interrupted() && isRunning()){
			try {
				selector.select();
				//System.out.println("return select");
				
				Iterator<SelectionKey> iKeys = selector.selectedKeys().iterator();
				
				
				while(iKeys.hasNext()){
					SelectionKey key = iKeys.next();
					
					if(key.channel() == channel && key.isValid()){
						
						if(key.isReadable()) synchronized(readLock){
							readLock.notifyAll();
						}
						
						if(key.isWritable()) synchronized(writeLock){
							writeLock.notifyAll();
							channel.register(selector, SelectionKey.OP_READ);
						}
						
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private synchronized boolean isRunning() {
		return running ;
	}
	
}
