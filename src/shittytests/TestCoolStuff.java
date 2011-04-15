package shittytests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;

import fun.useless.curses.Curses;
import fun.useless.curses.term.Terminal;
import fun.useless.curses.term.io.TelnetIO;
import fun.useless.curses.term.termcap.TermCap;
import fun.useless.curses.ui.WindowManager;

//import jcurses.ui.Label;


public class TestCoolStuff extends Thread{
	
	private TermCap  capdb;
	private Terminal term;
	private Curses   crs;
	
	private boolean  running = true;
	private TelnetIO telnet = null;
	
	private TreeMap<Character,MenuItem> menu;
	
	public abstract class MenuItem{
		private String menuName;
		public MenuItem(String name){ menuName = name; }
		@Override public String toString(){ return menuName; }
		public abstract void select();
	}
	public TestCoolStuff() throws IOException{
		this(null,null,null);
	}
	public TestCoolStuff(InputStream i,OutputStream o, InterruptibleChannel c) throws IOException{
		
		createMenu();
		
		capdb = new TermCap("termcap.src");
		
		//We need a terminal type. try to get the correct one
		if(i==null || o==null){
			String trm=System.getenv("TERM");
			
			if(trm==null)
				trm = "ansi";
			
			term = new Terminal(capdb.getTermType(trm));			
		}else{
			TelnetIO preTelnet = new TelnetIO(i, o);
			
			//Everyone suppresses GA 
			preTelnet.will(TelnetIO.SUPPRESS_GOAHEAD);
			preTelnet.do_(TelnetIO.SUPPRESS_GOAHEAD);
			
			//Get terminal type to send right escape seq. (defaults to ansi)
			String trm = preTelnet.autoNegotiateTerminal();
			if(trm==null)
				trm = "ansi";
			
			//Tries to suppress line mode with telnet protocol.
			//The idea in telnet is : if I say i WILL, the client does not
			preTelnet.will(TelnetIO.ECHO);
			preTelnet.will(TelnetIO.LINEMODE);

			term = new Terminal(capdb.getTermType(trm),c);
			telnet = new TelnetIO(term.getInputStream(), term.getOutputStream());
			term.replaceInputStream(telnet.makeHookedInputStream());
		}
		
		crs  = new Curses(term);
		crs.noecho();
		crs.raw();
		crs.initColor();
		crs.civis();
	}
	
	private void displayMenu(){
		try{
			crs.bColor(4);
			crs.fColor(7);
			crs.clear();
			
			
			crs.printAt("Menu",1,1);
			crs.printAt("====",2,1);
			
			int col  = 4;
			int line = 4;
			for(Character k : menu.keySet())
				crs.printAt( k +": "+ menu.get(k), ++line, col);
			
			crs.printAt("        ^", 23, 4);
			crs.printAt("Choice: ",22,4);
			
		}catch(IOException e){
			e.printStackTrace();
			running = false;
		}
	}
	private void sayHello(){
		try{
			
			crs.bColor(7);
			crs.fColor(1);
			crs.clear();
			
			crs.printAt("Hello.", 12, 12);
			Thread.sleep(2000);
			crs.fColor(2);
			crs.printAt("Press a key.", 23, 12);
			getChar();
		}catch(IOException e){
			e.printStackTrace();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	private void rainbow(){
		try{
			crs.bColor(6);
			crs.clear();
			for(int i=0;i<5;i++){
				int c = i+1;
				crs.fColor(c);
				crs.bColor(c);
				for(int line=0;line<4;line++){
					for(int col=0;col<6;col++){
						int diff = line*(7-line);
						if(diff>=0){
							crs.printAt("#", line+i+2, 40 + col + diff);
							crs.printAt("#", line+i+2, 40 - col - diff);
						}
					}
				}
			}
			getChar();
		}catch(IOException e){
			e.printStackTrace();
			running = false;
		}
	}
	private void window(){
		try{
			
			WindowManager man = new WindowManager(crs);
			
			man.start();
		
		}catch(IOException e){
			e.printStackTrace();
			running = false;
		}
	}
	private void quit(){
		try{
			crs.cnorm();
			crs.bColor(0);
			crs.fColor(7);
			crs.clear();
		}catch(IOException e){
			e.printStackTrace();
		}
		running = false;
	}
	private void createMenu(){
		menu = new TreeMap<Character,MenuItem>();
		
		menu.put('1', new MenuItem("Say Hello") {
			
			@Override
			public void select() {
				sayHello();
			}
		});
		menu.put('2', new MenuItem("Rainbow") {
			
			@Override
			public void select() {
				rainbow();
			}
		});
		menu.put('3', new MenuItem("Window") {
			
			@Override
			public void select() {
				window();
			}
		});
		menu.put('x', new MenuItem("Exit") {
			
			@Override
			public void select() {
				quit();
			}
		});
	}
	private char getChar() throws IOException{
		return term.getChar();
	}
	public void run(){
		try{
			while(running){
				displayMenu();
				Character c = new Character(getChar());
				if(menu.containsKey(c))
					menu.get(c).select();
			}
			term.closeStreams();
		}catch(IOException iox){
			iox.printStackTrace();
		}
		
	}
	
	public static void serverMain(String[] args) throws Exception{
		int port = Integer.parseInt(args[0]);
		
		ServerSocketChannel server = ServerSocketChannel.open();
		server.socket().bind(new InetSocketAddress(port));
		
		SocketChannel client;
		
		System.out.println("Server mode listening.");
		
		while( ( client = server.accept() ) != null){
			Socket s = client.socket();
			TestCoolStuff coolStuff = new TestCoolStuff(s.getInputStream(),s.getOutputStream(),client);
			coolStuff.start();
		}
	
	}
	
	public static void main(String[] args){

		if(args.length>0){
			try{
				serverMain(args);
				return;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Server failure. Trying stdio in 2s.");
				try {  Thread.sleep(2000); } catch (InterruptedException e1){}
			}
		}
		
		try{
			TestCoolStuff t = new TestCoolStuff();
			t.run();
		}catch(IOException iox){
			iox.printStackTrace();
		}
		
	}
}
