package textmode.xfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import textmode.xfer.zm.util.ZModemReceive;
import textmode.xfer.zm.util.ZModemSend;


public class ZModem {
	
	private InputStream netIs;
	private OutputStream netOs;

	public ZModem(InputStream netin,OutputStream netout){
		netIs  = netin;
		netOs  = netout;
		
	}
	
	public void receive(File destDir) throws IOException{
		netOs.write("Send a file now.\n".getBytes());
		netOs.flush();
		
		
		ZModemReceive sender = new ZModemReceive(destDir, netIs, netOs);
		
		sender.receive();
		
		netOs.flush();
		
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	public void send(Map<String,File> lst) throws IOException{
		netOs.write("Start the client-side download now.\n".getBytes());
		netOs.flush();
		
		
		ZModemSend sender = new ZModemSend(lst, netIs, netOs);
		
		sender.send();
		
		netOs.flush();
		
	
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
