package fun.useless.curses.net;


import java.io.IOException;
import java.io.InvalidClassException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import fun.useless.curses.application.Screen;
import fun.useless.curses.application.ScreenFactory;
import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.io.TelnetIO;
import fun.useless.curses.term.termcap.TermCap;



public class TelnetServer {

	private TermCap termcap;
	private ScreenFactory appFactory;
	
	public TelnetServer(String termCapFile,ScreenFactory factory) throws IOException{
		termcap = new TermCap(termCapFile);
		appFactory = factory;
	}
	
    /**
     * Work with nio to have interruptible channel
     * @param port
     * @throws IOException
     */
	public void acceptConnections(int port) throws IOException{
		
		ServerSocketChannel server = ServerSocketChannel.open();
		server.socket().bind(new InetSocketAddress(port));

		SocketChannel client;
		
		while( ( client = server.accept() ) != null){
			Socket s = client.socket();
			
			//Create a telnet client to negotiate the options
			TelnetIO telnet = new TelnetIO(s.getInputStream(), s.getOutputStream());
			
			//Everyone suppresses GA 
			telnet.will(TelnetIO.SUPPRESS_GOAHEAD);
			telnet.do_(TelnetIO.SUPPRESS_GOAHEAD);
			
			//Get terminal type to send right escape seq. (default to ansi)
			String termTypeString = telnet.autoNegotiateTerminal();
			
			if(termTypeString==null) termTypeString = "ansi";
			
			//Suppresses line mode with telnet protocol.
			//The idea in telnet is : if I say i WILL, the client WONT
			telnet.will(TelnetIO.ECHO);
			telnet.will(TelnetIO.LINEMODE);
			
			client.configureBlocking(false);
			try{
				Terminal term = new Terminal(termcap.getTermType(termTypeString),client);
			
				//Hook the terminal stream with a new Telnet client to respond to protocol commands.
				TelnetIO telnetclient2 = new TelnetIO(term.getInputStream(), term.getOutputStream());
				term.replaceInputStream(telnetclient2.makeHookedInputStream());
				
				//Creating an application may require terminal IO. it could fail and return null.
				Screen app = appFactory.createScreen(term);
				if(app!=null) app.start();
			}catch(InvalidClassException ice){
				ice.printStackTrace();
			}
		}
	}
}
