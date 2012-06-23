package textmode.curses.term.io;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OperationLock extends Thread {
	
	private static int ThId = 0;
	
	private SelectableChannel channel;
	private Selector selector;
	
	private Queue<Thread> accessors   = new ConcurrentLinkedQueue<Thread>();
	private ConcurrentHashMap<Thread,Boolean>  throwers = new ConcurrentHashMap<Thread,Boolean>();

	private AtomicBoolean running   = new AtomicBoolean(true);
	private AtomicBoolean ready = new AtomicBoolean(false);
	private Lock lock = new ReentrantLock();
	private Condition canSelect = lock.newCondition();
	private Condition canDo   = lock.newCondition();
	
	private int op;
	
	
	public OperationLock(SelectableChannel chan, int op) throws IOException {
		super("OpLock-"+op+"-"+(++ThId));
		setDaemon(true);
		channel = chan;
		this.op = op;

		selector = channel.provider().openSelector();
		channel.register(selector, op);
	}
	
    
	public void wantDo() throws InterruptedException, IOException{
		try{
			Thread th = Thread.currentThread();
			lock.lock();
			if(!selector.isOpen())
				throw new IOException("Selector is closed");
				
			if(!accessors.contains(th))
				accessors.add(th);
			
			if(!ready.get())
				canSelect.signal();
					
			while ( !(accessors.peek() == th && (ready.get() || throwers.containsKey(th))) )
				canDo.await();
			
			
			if(throwers.containsKey(th)){
				throwers.remove(th);
				done();
				throw new InterruptedException();
			}
		
		}finally{
			lock.unlock();
		}
	}
	
	public void done(){
		try{
			lock.lock();
			Thread th = Thread.currentThread();
			
			if(accessors.remove(th)){
				ready.set(false);
				canSelect.signal();
			}
			
		}finally{
			lock.unlock();
		}
	}
	
	public SelectableChannel channel(){
		return channel;
	}
	
	public void stopSelecting(){
		running.set(false);
		wakeup();
		
		try {
			lock.lock();
			selector.close();
			canSelect.signal();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
	public void throwAllAccessors(){
		for(Thread t: accessors)
			throwers.put(t, true);
	}
	
	public void wakeup(){
		
		throwAllAccessors();
		selector.wakeup();
		
		try{
			lock.lock();

			canDo.signal();
			
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public void run() {
		
		while(isRunning()){
			try {
				
				lock.lock();
				
				while( ready.get() && isRunning() )
					canSelect.await();
				
				if(isRunning()){
					selector.select();
					
					Iterator<SelectionKey> iKeys = selector.selectedKeys().iterator();
				
					while(iKeys.hasNext()){
						SelectionKey key = iKeys.next();
					
						if(key.channel() == channel && key.isValid()){
						
							if((key.readyOps() & op)!=0){
								ready.set(true);
								canDo.signal();
							}
						
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				lock.unlock();
			}
		}
	}

	private boolean isRunning() {
		return running.get() ;
	}
	
}
