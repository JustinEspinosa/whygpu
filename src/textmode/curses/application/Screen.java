package textmode.curses.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import textmode.curses.Curses;
import textmode.curses.CursesFactory;
import textmode.curses.term.Terminal;
import textmode.curses.ui.WindowManager;



public class Screen extends Thread{

	private static Map<String,Session> sessions = new HashMap<String,Session>();
	
	private static Session createSession(){
		Session s = new Session();
		sessions.put(sessionId(s),s);
		return s;
	}
	
	private static String sessionId(Session s){
		return String.format("%08x", s.hashCode() );
	}
	
	public static Session currentSession(){
		return sessions.get(Thread.currentThread().getThreadGroup().getName());
	}
	
	private Curses        curses;
	private WindowManager winMan;
	private Session       session;
	
	public Screen(Terminal t, CursesFactory cf) throws IOException{
		this(t,cf,null);
	}
	
	public Curses curses(){
		return curses;
	}
	
	public Screen(Terminal t,CursesFactory cf, RootApplication root) throws IOException{
		this(t,cf,root,createSession());
	}
	private Screen(Terminal t,CursesFactory cf, RootApplication root, Session s) throws IOException{
		super(new ThreadGroup(sessionId(s)),"ScreenThread");
		session = s;
		curses = cf.createCurses(t);
		curses.noecho();
		curses.raw();
		curses.initColor();
		curses.civis();
		//curses.setUtf8(true);
		winMan = new WindowManager(curses, root);
		
		session.put(WindowManager.class.getName(), winMan);
		session.put(Curses.class.getName(), curses);
		session.put(Screen.class.getName(), this);
		
	}
	
	public final Session session(){
		return session;
	}
	
	protected WindowManager getWindowManager(){
		return winMan;
	}
	
	public void submitApplication(ApplicationFactory factory){
		winMan.submitApplicationToMenu(factory);
	}
	
	/**
	 * May be overridden if one wants to catch the exceptions
	 */
	@Override
	public void run(){
		
		WindowManager winMan = getWindowManager();
		try {
			winMan.start();
		} catch (IOException e) { 
		}finally{
			winMan.stop();
		}
		
	}
}
