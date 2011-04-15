package fun.useless.curses.test;

import java.io.IOException;

import fun.useless.curses.net.TelnetServer;

public class TestTelnetMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			TelnetServer server = new TelnetServer("termcap.src", new TestScreenFactory());
			System.out.println("Listening on TCP:9003");
			server.acceptConnections(9003);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}

}
