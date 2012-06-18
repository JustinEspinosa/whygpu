package textmode.curses.term.io;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class NoWaitIOLock extends Thread {
	
	private static int ThId = 0;
	
	private SelectableChannel channel;
	private Selector selector;
	
	private Queue<Thread> writers   = new ConcurrentLinkedQueue<Thread>();
	private Queue<Thread> readers   = new ConcurrentLinkedQueue<Thread>();
	private AtomicBoolean running   = new AtomicBoolean(true);
	private AtomicBoolean mustThrow = new AtomicBoolean(false);
	private AtomicBoolean canRead   = new AtomicBoolean(false);
	private AtomicBoolean canWrite  = new AtomicBoolean(false);

	private SelectionKey myKey;
	
	public NoWaitIOLock(SelectableChannel chan) throws IOException {
		super("IOLock-"+(++ThId));
		setDaemon(true);
		channel = chan;
		selector = channel.provider().openSelector();
		channel.configureBlocking(false);
		myKey = channel.register(selector, SelectionKey.OP_READ);
	}
	
    public void printStackTrace(StackTraceElement[] trace) {
       for (int i=0; i < trace.length; i++)
    	   System.out.println(trace[i]);
    }
	
    private void freeNextReader(){
    	LockSupport.unpark(readers.peek());
    }
    
    private void freeNextWriter(){
    	LockSupport.unpark(writers.peek());
    }
    
	public void wantRead() throws InterruptedException{
		Thread th = Thread.currentThread();
		if(!readers.contains(th))
			readers.add(th);
				
		while ( !(readers.peek() == th && (canRead.get() || mustThrow.get())) ){
			LockSupport.park();
		}
				
		if(mustThrow.get()){
			doneRead();
			throw new InterruptedException();
		}
	}
	
	public void doneRead(){
		Thread th = Thread.currentThread();
		if(readers.peek()==th && readers.remove(th)){
			canRead.set(false);
			LockSupport.unpark(this);
		}
	}
	
	public void wantWrite() throws InterruptedException{

		if( (myKey.interestOps() & SelectionKey.OP_WRITE) == 0){
			myKey = myKey.interestOps(SelectionKey.OP_WRITE|SelectionKey.OP_READ);
			LockSupport.unpark(this);
		}
			
		Thread th = Thread.currentThread();
		if(!writers.contains(th))
			writers.add(th);
		
		while ( !(writers.peek() == th && (canWrite.get() || mustThrow.get())) ){
			LockSupport.park();
		}
			
		if(mustThrow.get()){
			doneWrite();
			throw new InterruptedException();
		}
		
	}
	
	public void doneWrite(){
		Thread th = Thread.currentThread();
		if( writers.peek()==th && writers.remove(th) ){
			canWrite.set(false);
			if(writers.isEmpty())
				myKey = myKey.interestOps(SelectionKey.OP_READ);
			LockSupport.unpark(this);
		}
	}
	
	
	public SelectableChannel channel(){
		return channel;
	}
	
	public void stopSelecting(){
		running.set(false);
		wakeup();
	}
	
	public void wakeup(){
		
		selector.wakeup();
		mustThrow.set(true);
		
		for(Thread r : readers)
			LockSupport.unpark(r);
		
		for(Thread w : writers)
			LockSupport.unpark(w);

	}
	
	@Override
	public void run() {
		while(!interrupted() && isRunning()){
			try {
				
				if( (myKey.interestOps() & SelectionKey.OP_READ) !=0 && canRead.get() ){
					if( (myKey.interestOps() & SelectionKey.OP_WRITE) !=0){
						if(canWrite.get()) 
							LockSupport.park();
					}else{
						LockSupport.park();
					}
				}
				
				selector.select();
				
				if(isRunning()){
					mustThrow.set(false);
					
					Iterator<SelectionKey> iKeys = selector.selectedKeys().iterator();
				
					while(iKeys.hasNext()){
						SelectionKey key = iKeys.next();
					
						if(key.channel() == channel && key.isValid()){
						
							if(key.isReadable()){
								canRead.set(true);
								freeNextReader();
							}
						
							if(key.isWritable()){
								canWrite.set(true);
								freeNextWriter();
							}
						
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private boolean isRunning() {
		return running.get() ;
	}
	
}
