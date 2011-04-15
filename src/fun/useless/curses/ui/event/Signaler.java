package fun.useless.curses.ui.event;

public class Signaler extends Thread{
 
	private SignalReceiver target;
	private int time;
	
	public Signaler(SignalReceiver src,int timeout) {
		target = src;
		time = timeout;
		start();
	}
	
	@Override
	public void run(){
		try{ sleep(time); }catch(InterruptedException ie){}
		target.signalReceived();
	}

}
