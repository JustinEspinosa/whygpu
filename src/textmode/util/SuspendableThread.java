package textmode.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class SuspendableThread extends Thread {
	private AtomicBoolean suspended = new AtomicBoolean(false);
	
	public SuspendableThread(ThreadGroup grp, String n){
		super(grp,n);
	}
	
	public final void pause(){
		suspended.set(true);
	}
	
	public final void unpause(){
		suspended.set(false);
		LockSupport.unpark(this);
	}

	protected final void pauseIfNeeded(){
		Thread current = Thread.currentThread();
		if(current != this)
			return;
		if(suspended.get())
			LockSupport.park();
	}
}
