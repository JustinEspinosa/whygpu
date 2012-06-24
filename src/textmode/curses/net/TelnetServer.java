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
	
	private static class TelnetSessionAliveKeeper extends Thread{
		private TelnetIO telnet;
		private long interval;
		
		private TelnetSessionAliveKeeper(TelnetIO telnet,long interval){
			super("Keep alive "+telnet);
			this.telnet = telnet;
			this.interval = interval;
		}
		@Override
		public void run() {
			try{
				while(true){
					sleep(interval);
					telnet.keepAlive();
				}
			}catch(IOException e){	
			}catch(InterruptedException e){
			}finally{
				
				try {
					telnet.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private class TelnetKickStarter extends Thread{
		private SocketChannel client;
		private SSLContext SSLCtx;
		
		private TelnetKickStarter(SocketChannel client,SSLContext ctx){
			this.client = client;
			this.SSLCtx = ctx;
		}
		
		@Override
		public void run(){
			boolean secure = (SSLCtx!=null);
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
						return;
					}
				
				}else{
					s = new GeneralSocketIO(client);
				}
			
			
				//Create a telnet client to negotiate the options
				TelnetIO telnet = new TelnetIO(logger,s.getInputStream(), s.getOutputStream());
			
				(new TelnetSessionAliveKeeper(telnet, 5000)).start();

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
				//TelnetIO telnetclient2 = new TelnetIO(logger,term.getInputStream(), term.getOutputStream());
				
				telnet.enableResizeHandling(term);
				
				term.replaceInputStream(telnet.makeHookedInputStream());
				term.replaceOutputStream(telnet.makeHookedOutputStream());
				
				//Creating an application may require terminal IO. it could fail and return null.
				Screen app = appFactory.createScreen(term, cursesFact);
				if(app!=null) app.start();
				
			}catch(IOException ioe){
				try {
					client.close();
				} catch (IOException e) {
				}
				System.out.println("A connection was terminated unexpectedly.");
				ioe.printStackTrace();
			}
		}
	}
	
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
			(new TelnetKickStarter(client, SSLCtx)).start();
		}
	}
}
