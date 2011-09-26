package fun.useless.curses.test;

import java.io.IOException;

import fun.useless.curses.DefaultCursesFactory;
import fun.useless.curses.net.TelnetServer;

public class TestTelnetMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int port = 9003;
		
		try {
		
			if(args.length>0){
				try{
					port = Integer.parseInt(args[0]);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
			
			TelnetServer server = new TelnetServer(new DefaultCursesFactory("termcap.src"), new TestScreenFactory());
			System.out.println("Listening on IP:0.0.0.0, TCP:"+port+".");
			server.acceptConnections(port);
			
		} catch (IOException e) {
			
			System.out.println("FATAL CATASTROPHIC ERROR! CALAMITY!");
			e.printStackTrace();
			System.exit(-1);
			
		}
		
	}

}
