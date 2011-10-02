package textmode.curses.net;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import textmode.curses.CursesFactory;
import textmode.curses.application.Screen;
import textmode.curses.application.ScreenFactory;
import textmode.curses.term.Terminal;
import textmode.curses.term.io.TelnetIO;




public class TelnetServer {
	
	private ScreenFactory appFactory;
	private CursesFactory cursesFact;
	private Logger logger = null;
	
	
	public TelnetServer(Logger l,CursesFactory cFact,ScreenFactory factory) throws IOException{
		logger = l;
		appFactory = factory;
		cursesFact = cFact;
	}
	
	public TelnetServer(CursesFactory cFact,ScreenFactory factory) throws IOException{
		this(null,cFact,factory);
	}

	
	private boolean canLog(){
		return logger != null;
	}
	
    /**
     * Works with nio to have interruptible channel
     * @param port
     * @throws IOException
     */
	public void acceptConnections(int port) throws IOException{
		acceptConnections(port,null);
	}
	
    /**
     * TLS enabled
     * @param port
     * @param secure Uses TLS if true
     * @throws IOException
     */
	public void acceptConnections(int port, SSLContext SSLCtx) throws IOException{
		boolean secure = (SSLCtx!=null);
		
		if(canLog())
			logger.info( ((secure)?"TLS ":"") + "server started on "+port);
		
		ServerSocketChannel server = ServerSocketChannel.open();
		server.socket().bind(new InetSocketAddress(port));

		SocketChannel client;
		
		while( ( client = server.accept() ) != null){
			SocketIO s;
			try{
				
				if(canLog())
					logger.info( ((secure)?"TLS ":"") + "server accepted one.");
			
				if(secure){
					try{
						s = new TLSGeneralSocketIO(logger, client, SSLCtx);
					}catch(SSLException e){
						ByteBuffer buff = ByteBuffer.allocateDirect(256);
						buff.put("Sorry, this server requires TLS.\n".getBytes());
						buff.flip();
						client.write(buff);
						client.close();
						continue;
					}
				
				}else{
					s = new GeneralSocketIO(client);
				}
			
			
				//Create a telnet client to negotiate the options
				TelnetIO telnet = new TelnetIO(logger,s.getInputStream(), s.getOutputStream());
			
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
		
			
				Terminal term = cursesFact.createTerminal(termTypeString,s);
			
				//Hook the terminal stream with a new Telnet client to respond to protocol commands.
				TelnetIO telnetclient2 = new TelnetIO(logger,term.getInputStream(), term.getOutputStream());
				term.replaceInputStream(telnetclient2.makeHookedInputStream());
				term.replaceOutputStream(telnetclient2.makeHookedOutputStream());
				
				//Creating an application may require terminal IO. it could fail and return null.
				Screen app = appFactory.createScreen(term, cursesFact);
				if(app!=null) app.start();
			}catch(IOException ioe){
				client.close();
				System.out.println("A connection was terminated unexpectedly.");
			}
		}
	}
}
