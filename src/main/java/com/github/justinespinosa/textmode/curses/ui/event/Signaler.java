package com.github.justinespinosa.textmode.curses.ui.event;

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
		try{ sleep(time); }catch(InterruptedException ie){ ie.printStackTrace(); }
		target.signalReceived();
	}

}
