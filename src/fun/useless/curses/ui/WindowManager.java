package fun.useless.curses.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;
import java.util.Vector;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.application.ApplicationFactory;
import fun.useless.curses.application.RootApplication;
import fun.useless.curses.ui.components.Component;
import fun.useless.curses.ui.components.MenuBar;
import fun.useless.curses.ui.components.MenuPlane;
import fun.useless.curses.ui.components.PopUp;
import fun.useless.curses.ui.components.RootPlane;
import fun.useless.curses.ui.components.Window;
import fun.useless.curses.ui.components.WindowPlane;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.CursorControlEvent;
import fun.useless.curses.ui.event.Event;
import fun.useless.curses.ui.event.EventReceiver;
import fun.useless.curses.ui.event.RedrawEvent;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.TerminalInputEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;


public class WindowManager implements EventReceiver {
	
	private final class TerminalInputEventer{
		private Vector<Integer> history = new Vector<Integer>();
		private TerminalInputEventer(){}
		
		private void put(int c){
			history.add(new Integer(c));
			checkHistory();
		}
		private int[] vectorToArray(Vector<Integer> h){
			int[] a=new int[h.size()];
			for(int n=0;n<h.size();n++)
				a[n] = h.elementAt(n).intValue();
			
			return a;
		}
		private void checkHistory(){
			int[] ah = vectorToArray(history);
			int num = detector.countMatches(ah);
			if(num>0){
				if(num==1){
					int m = detector.getMatch(ah);
					if(m>-1){
						evQueue.put(new TermKeyEvent(WindowManager.this, m));
						history.clear();
					}
				}
			}else{
				for(Integer i: history)
					evQueue.put(new CharacterCodeEvent(WindowManager.this, (char)i.intValue()));
				history.clear();
			}
		}
	}
	
	private final class EventQueue{
		private Vector<Event> queue = new Vector<Event>();
		private EventQueue() { }
		/**
		 * Put a new event
		 * @param e
		 */
		private synchronized void put(Event e){
			queue.add(e);
			notify();
		}

		/**
		 * Get the current event and removing (making next event current)
		 * @return
		 */
		private synchronized Event pop() throws InterruptedException{
			if(queue.size()<1)
				wait();
			
			Event r = queue.elementAt(0);
			queue.remove(0);
			return r;
		}
	}
	
	private final class RedrawThread extends Thread{
		private boolean redrawrunnig = true;
		private RectangleBuffer buffer = new RectangleBuffer();
		private RedrawThread() { }
		
		
		public void addRect(Rectangle r){
			buffer.addToArea(r);
		}
		private synchronized boolean isRunning(){
			return redrawrunnig;
		}
		private synchronized void stopMe(){
			redrawrunnig = false;
		}
		@Override
		public void run(){
			while(this.isRunning()){
				try{
					Rectangle[] rArray= buffer.getArea();
					
					for(int n=0;n<rArray.length;n++){
						redraw(rArray[n]);
					}
					
					sleep(50);
				}catch(Exception e){
					this.stopMe();
				}
			}
		}
		
	}

	private final class UIThread extends Thread{
		private boolean uirunning = true;
		
		private UIThread() { }
		
		private synchronized boolean isRunning(){
			return uirunning;
		}
		
		private synchronized void stopMe(){
			uirunning = false;
		}
		
		@Override
		public void run(){
			while(this.isRunning()){
				try{
					Event ev = evQueue.pop();
					if(ev!=null)
						processEvent(ev);
				}catch(InterruptedException ie){
					stopMe();
				}
			}
		}
	}


	private Vector<Application>  runningApps = new Vector<Application>();
	private UIThread             intThread   = new UIThread();
	private RedrawThread         rdThread    = new RedrawThread();
	private EventQueue           evQueue     = new EventQueue();
	private TerminalInputEventer eventer     = new TerminalInputEventer();
	private EscapeDetector       detector    = new EscapeDetector();
	private boolean              running     = false;
	
	private RootApplication      rootApplication;
	private Application          currentApp;
	private RootPlane<?>         activePlane;
	private InputStream          termInput;
	private WindowPlane          windowPlane;
	private MenuPlane            menuPlane;
	private Curses               crs;
	
	public WindowManager(Curses c) throws IOException{
		crs = c;
		menuPlane       = new MenuPlane(this,0, 0, crs.lines(), crs.cols());
		windowPlane     = new WindowPlane(this,0,0,crs.lines(),crs.cols());
		rootApplication = new RootApplication();
		activePlane     = windowPlane;
		termInput       = crs.getTerminal().getInputStream();
	}
	
	
	private void focusMenuBar(MenuBar m){
		menuPlane.bringToFront(m);
		activePlane = menuPlane;
	}
	
	public void releaseFocus(RootPlane<?> p){
		if(p == activePlane){
			if(p == menuPlane){
				activePlane = windowPlane;
				menuPlane.lostFocus();
			}
			if(p == windowPlane){
				activePlane = menuPlane;
				windowPlane.lostFocus();
			}
		}
	}
	
	public MenuBar newMenuBar(){
		MenuBar m = new MenuBar(this);
		menuPlane.addChild(m);
		return m;
	}
	
	public PopUp newPopUp(int cols){
		PopUp p = new PopUp(cols,this);
		menuPlane.addChild(p);
		return p;
	}
	
	public Position getNextWindowPosition(){
		return windowPlane.getNextWindowPostion();
	}
	
	public void manageWindow(Window w){
		windowPlane.addAndMakeActive(w);
	}

	
	public int getWidth(){
		try {
			return crs.cols();
		} catch (IOException e) {
			return 80;
		}
	}
	
	/**
	 * It is called by the base class Application, no need to call it manually
	 * @param a
	 */
	public void registerApplication(Application a){
		runningApps.add(a);
	}
	
	/**
	 * It is called by the base class Application, no need to call it manually
	 * @param a
	 */
	public void unregisterApplication(Application a){
		runningApps.remove(a);
		nextApplication();
	}
	
	
	public void activateApplication(Application a){
		currentApp = a;
		windowPlane.informOfCurrentlyActiveApplication(currentApp);
		windowPlane.activateList(a.getWindows());
		menuPlane.bringToFront(a.getMenuBar(), false);
		windowPlane.refreshStatus();
		receiveEvent(new RedrawEvent(windowPlane, new Rectangle(windowPlane.getPosition(), windowPlane.getSize())));
	}
	
	private void nextApplication(){
		
		if(activePlane!=windowPlane)
			return;
		
		if(runningApps.size() == 0){
			activateApplication(null);
			return;
		}
		
		if(currentApp == null){
			activateApplication(runningApps.firstElement());
			return;
		}
		
		if(currentApp == runningApps.lastElement()){
			activateApplication(rootApplication);
			return;
		}
		
		int index = runningApps.indexOf(currentApp);
		activateApplication(runningApps.elementAt(index+1));
				
	}
	
	/**
	 * Receive all the events comming from the terminal stream.
	 */
	private void processEvent(Event e){
		

		if(e instanceof CursorControlEvent){
			try{
				CursorControlEvent cce = (CursorControlEvent)e;
				Position p = cce.getPosition();
				if(p!=null){
					if(e.getSource() instanceof Component){
						Position np = windowPlane.convertPosition(p,(Component)e.getSource());
						if(np==null) np = menuPlane.convertPosition(np,(Component)e.getSource());
						p=np;
					}
					if(p!=null) crs.cursorAt(p.getLine(), p.getCol());
				}
				if(cce.getCursorState())
					crs.cnorm();
				else
					crs.civis();
				
			}catch(IOException ioe){
			}
			return;
		}		
		if(e instanceof RedrawEvent){
			Rectangle r = ((RedrawEvent)e).getArea();
			if(e.getSource() instanceof Component){
				Rectangle nr = windowPlane.convertPosition(r,(Component)e.getSource());
					
				if(nr==null) 
					nr = menuPlane.convertPosition(r,(Component)e.getSource());
					
				r=nr;
			}
			if(r!=null) rdThread.addRect(r);

			return;
		}
		
		
		if(e instanceof TerminalInputEvent){
			if(e instanceof TermKeyEvent){
				TermKeyEvent e2=(TermKeyEvent)e;
				if(e2.getKey()==TermKeyEvent.NEXTAPP){
					nextApplication();
					return;
				}
				if(e2.getKey()==TermKeyEvent.MENU){
					if(activePlane == windowPlane){
						MenuBar m = null;
						
						if(currentApp!=null)
							m = currentApp.getMenuBar();
						
						if(m!=null) 
							focusMenuBar(m);
						
						return;
					}
					
				}
			}
			activePlane.processEvent(new UiInputEvent((TerminalInputEvent)e));
		}
		
		if(e instanceof UiEvent){
			windowPlane.processEvent((UiEvent)e);
		}
		
		/**** EXIT Hook ****/
		if(e instanceof TermKeyEvent)
			if( ((TermKeyEvent)e).getKey()==TermKeyEvent.EXIT )
				stop();
	}
	public synchronized void stop(){
		running = false;
		try{
			crs.rmcup();
			crs.getTerminal().writeCommand("me", crs.lines());
			crs.clear();
			crs.getTerminal().writeCommand("r1", crs.lines());
			crs.getTerminal().writeCommand("r2", crs.lines());
			crs.getTerminal().writeCommand("r3", crs.lines());
			try {Thread.sleep(50);} catch (InterruptedException e) {}
			crs.getTerminal().closeChannel();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		rdThread.stopMe();
		rdThread.interrupt();
		intThread.stopMe();
		intThread.interrupt();
	}
	public synchronized boolean isRunning(){
		return running;
	}
	private synchronized void begin(Thread caller){
		running = true;
	}
	public void start() throws IOException{
		if(isRunning()) return;
		
		begin(Thread.currentThread());
		crs.smcup();
		crs.showWindow(windowPlane);
		intThread.start();
		rdThread.start();
		rootApplication.begin(this);
		try{
			while(isRunning()){
				eventer.put(termInput.read());
			}
		}catch(ClosedChannelException cee){
		}
		
		
	}
	
	public ColorChar getTopCharAt(int line,int col){
		return menuPlane.getCharAt(line, col);
	}
	
	private void redraw(Rectangle r) throws IOException{
		redraw(r.getLine(),r.getCol(),r.getLines(),r.getCols());
	}
	
	private void redraw(int sLine,int sCol,int lines,int cols) throws IOException{
		crs.drawColorCharArray(windowPlane.getPartialContent(sLine, sCol, lines, cols), sLine, sCol);
	}
	
	public void submitApplicationToMenu(ApplicationFactory factory){
		rootApplication.addToApplicationMenu(factory);
	}
	
	public void postEvent(Event e){
		evQueue.put(e);
	}

	@Override
	public void receiveEvent(Event e) {
		if(isRunning()) postEvent(e);		
	}

	public void releaseWindow(Window w) {
		windowPlane.removeChild(w);
	}
	
}
