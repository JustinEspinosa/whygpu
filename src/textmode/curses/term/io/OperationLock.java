package textmode.curses.term.io;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OperationLock  {
	
	
	private SelectableChannel channel;
	private Selector selector;
	private AtomicBoolean mustThrow = new AtomicBoolean(false);

	private Queue<Thread> accessors   = new ConcurrentLinkedQueue<Thread>();

	private Lock lock = new ReentrantLock();
	private Condition canDo   = lock.newCondition();
	
	private int op;
	
	
	public OperationLock(SelectableChannel chan, int op) throws IOException {
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
				throw new IOException(new ClosedSelectorException());
				
			accessors.add(th);
					
			while ( !(accessors.peek() == th) )
				canDo.await();

			boolean selected=false;			
			while(!(mustThrow.get() || selected)){
				selector.select();
			
				for( SelectionKey key : selector.selectedKeys())
					if(key.channel() == channel && key.isValid() && (key.readyOps() & op)!=0)
						selected  = true;
			}
			
			
			if(mustThrow.get()){
				if(accessors.remove(th))
					canDo.signal();
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
			if(accessors.remove(th))
				canDo.signal();
			
		}finally{
			lock.unlock();
		}
	}
	
	public SelectableChannel channel(){
		return channel;
	}
	
	public void stopSelecting(){
		//wakeup();
		

		try {
			mustThrow.set(true);
			selector.close();
			
			lock.lock();
			
			canDo.signalAll();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
	public void wakeup(){
				
		try{
			mustThrow.set(true);
			selector.wakeup();
			
			lock.lock();
			
			mustThrow.set(false);
			canDo.signal();
			
		}finally{
			lock.unlock();
		}
	}

	
}
