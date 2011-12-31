package textmode.curses.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;
import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.TerminalResizedReceiver;
import textmode.curses.application.Application;
import textmode.curses.application.ApplicationFactory;
import textmode.curses.application.FactoryLocator;
import textmode.curses.application.RootApplication;
import textmode.curses.lang.ColorChar;
import textmode.curses.term.io.InterruptIOException;
import textmode.curses.ui.components.Component;
import textmode.curses.ui.components.MenuBar;
import textmode.curses.ui.components.MenuPlane;
import textmode.curses.ui.components.PopUp;
import textmode.curses.ui.components.RootPlane;
import textmode.curses.ui.components.Window;
import textmode.curses.ui.components.WindowPlane;
import textmode.curses.ui.event.CharacterCodeEvent;
import textmode.curses.ui.event.CursorControlEvent;
import textmode.curses.ui.event.Event;
import textmode.curses.ui.event.EventReceiver;
import textmode.curses.ui.event.RedrawEvent;
import textmode.curses.ui.event.ResolutionChangeEvent;
import textmode.curses.ui.event.StopUIThread;
import textmode.curses.ui.event.TermKeyEvent;
import textmode.curses.ui.event.TerminalInputEvent;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;
import textmode.curses.ui.look.ColorTheme;
import textmode.xfer.ZModem;



public class WindowManager implements EventReceiver, TerminalResizedReceiver {
	
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
		private RedrawThread(ThreadGroup thg) { 
			super(thg,"Redraw");
			setDaemon(true);
		}
		
		
		public void addRect(Rectangle r){
			buffer.addToArea(r);
		}
		
		public void wakeup(){
			buffer.wakeup();
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
					
				}catch(InterruptIOException no){
					no.printStackTrace();
					
				}catch(Exception e){
					e.printStackTrace();
					WindowManager.this.stop();
					
				}finally{

					boolean wmIsSuspended = false;
					
					synchronized(WindowManager.this){
						wmIsSuspended = suspended;
						WindowManager.this.notify();
					}
					
					if(wmIsSuspended){
						synchronized(this){
							try {
								wait();
							} catch (InterruptedException e) { 
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private final class UIThread extends Thread{
		private boolean uirunning = true;
		
		private UIThread(ThreadGroup thg) { 
			super(thg,"EventConsumer");
			setDaemon(true);
		}
		
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
					if(ev!=null){
						if(ev instanceof StopUIThread)
							stopMe();
						else
							processEvent(ev);
					}

				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	}


	private Vector<Application>  runningApps = new Vector<Application>();
	private EventQueue           evQueue     = new EventQueue();
	private TerminalInputEventer eventer     = new TerminalInputEventer();
	private EscapeDetector       detector    = new EscapeDetector();
	private boolean              running     = false;
	private FactoryLocator       appList     = new FactoryLocator();
	private Object               readLock    = new Object();
	
	private UiEventProcessorFactory processorFactory = new UiEventProcessorFactory() {
		public UiEventProcessor createProcessor(UiEvent e, RootPlane<?> plane) {
			return new UiEventProcessor(e,plane);
		}
	};
	
	private RootApplication      rootApplication;
	private Application          currentApp;
	private RootPlane<?>         activePlane;
	private InputStream          termInput;
	private WindowPlane          windowPlane;
	private MenuPlane            menuPlane;
	private Curses               crs;
	private UIThread             intThread;
	private RedrawThread         rdThread; 
	private boolean suspended;
	
	public WindowManager(Curses c) throws IOException{
		this(c,null);
	}
	
	public WindowManager(Curses c, RootApplication root) throws IOException{
		crs = c;
		menuPlane       = new MenuPlane(this,crs,new Position(0, 0),new Dimension(crs.lines(), crs.cols()));
		windowPlane     = new WindowPlane(this,crs,new Position(0,0),new Dimension(crs.lines(),crs.cols()));
		
		if(root==null)
			root = new RootApplication();
		
		rootApplication = root;
		activePlane     = windowPlane;
		termInput       = crs.getTerminal().getInputStream();
		
		crs.wantsResizedNotification(this);
	}	
	
	public void setProcessorFactory(UiEventProcessorFactory factory){
		processorFactory = factory;
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
		MenuBar m = new MenuBar(crs,this);
		menuPlane.addChild(m);
		return m;
	}
	
	public PopUp newPopUp(int cols){
		PopUp p = new PopUp(crs,cols,this);
		menuPlane.addChild(p);
		menuPlane.refresh();
		return p;
	}
	
	/**
	 * Only gives between 10% and 90%, perc is stuck to boundaries
	 * @param perc
	 * @return
	 */
	public Dimension percentOfScreen(double perc){
		if(perc< 0.1) perc = 0.1;
		if(perc> 0.9) perc = 0.9;
		
		perc = Math.sqrt(perc);
		
		return getScreenSize().scale(perc);
	}
	
	public Dimension getScreenSize(){
		try {
			return new Dimension(crs.lines(),crs.cols());
		} catch (IOException e) {}
			return new Dimension(24,80);
	}
	
	public Position getNextWindowPosition(){
		return windowPlane.getNextWindowPostion();
	}
	
	public void manageWindow(Window w){
		windowPlane.addAndMakeActive(w);
	}

	public Window topMostWindow(Application app){
		return windowPlane.getTopMostWindow(app);
	}
	
	public int getWidth(){
		try {
			return crs.cols();
		} catch (IOException e) {
			return 80;
		}
	}
	
	
	private void suspendRead(){
		crs.getTerminal().wakeup();
	}
	
	private void suspendRedraw(){
		rdThread.wakeup();
	}
	
	public void suspend(){
		
		synchronized(this){
			suspended = true;
		}
		
		suspendRedraw();
		suspendRead();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e){
		}
		
		try {
			crs.rmcup();
			crs.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void resumeRead(){
		synchronized(readLock){
			readLock.notify();
		}
	}
	
	private void resumeRedraw(){
		synchronized(rdThread){
			rdThread.notify();
		}
	}
	
	public void resume(){
		
		synchronized(this){
			suspended = false;
		}
		
		try {
			crs.smcup();
			crs.invalidateDoubleBuffering();
			crs.showWindow(windowPlane);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resumeRead();
		
		resumeRedraw();
		
	}
	
	public ZModem createZModem(){
		return new ZModem(crs.getTerminal().getInputStream(),crs.getTerminal().getOutputStream());
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
		
		releaseFocus(menuPlane);
		runningApps.remove(a);
			
		nextApplication();
	}
	
	
	public void activateApplication(Application a){
		
		currentApp = a;
		
		windowPlane.informOfCurrentlyActiveApplication(currentApp);
		
		windowPlane.activateList(currentApp.getWindows());
		
		menuPlane.bringToFront(currentApp.getMenuBar(), false);
		
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
	 * Receives all the events coming from the terminal stream.
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
				ioe.printStackTrace();
			}
			return;
		}	
		
		
		if(e instanceof RedrawEvent){
			
			Rectangle r = ((RedrawEvent)e).getArea();
			
			if(e.getSource() instanceof Component){
				Rectangle nr = windowPlane.convertPosition(r,(Component)e.getSource());
					
				if(nr==null) 
					nr = menuPlane.convertPosition(r,(Component)e.getSource());
				
				if(nr!=null) 	
					r=nr;
			}
			
			if(r!=null)
				rdThread.addRect(r);

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
			(processorFactory.createProcessor(new UiInputEvent((TerminalInputEvent)e),activePlane)).start();
		}
		
		if(e instanceof ResolutionChangeEvent){

			menuPlane.setSize(((ResolutionChangeEvent) e).size());
			windowPlane.setSize(((ResolutionChangeEvent) e).size());
			
			menuPlane.refresh();
			windowPlane.refresh();
			
			(processorFactory.createProcessor((UiEvent)e,menuPlane)).start();
		}
		
		if(e instanceof UiEvent){
			(processorFactory.createProcessor((UiEvent)e,windowPlane)).start();
		}
		
		/***** OLD EXIT Hook:
		 **** the EXIT Key is now disabled
		 *** to give a chance to the applications to get closed properly.
		 **
		 * if(e instanceof TermKeyEvent && ((TermKeyEvent)e).getKey()==TermKeyEvent.EXIT )
		 *		stop();
		 */
	}
	public synchronized void stop(){
		
		rdThread.stopMe();
		intThread.stopMe();
		running = false;
		
		postEvent(new StopUIThread(this));
		
		rdThread.wakeup();
		
		try{
			crs.rmcup();
			crs.clear();
		}catch(IOException ioe){
		}

		try {
			crs.getTerminal().closeChannel();
		} catch (IOException e) {
		}

	}
	public synchronized boolean isRunning(){
		return running;
	}
	private synchronized void begin(Thread caller){
		running = true;
		intThread   = new UIThread(caller.getThreadGroup());      
		rdThread    = new RedrawThread(caller.getThreadGroup());  
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
				try{
					
					int b = termInput.read();
					if(b==0xffffffff)
						break;
					
					eventer.put(b);
				
				}catch(InterruptIOException exp){
					
					//exp.printStackTrace();
					
					synchronized(readLock){					
						if(suspended){
							try{
								readLock.wait();
							}catch(InterruptedException ie){
								ie.printStackTrace();
							}
						}
					}
					
				}
			}
		}catch(ClosedChannelException cee){
			cee.printStackTrace();
		}
		
		if(isRunning())
			stop();
	}
	
	public ColorChar getTopCharAt(Position p){
		return menuPlane.getCharAt(p);
	}
	
	private void redraw(Rectangle r) throws IOException{
		redraw(r.getOrigin(),r.getDimension());
	}
	
	private void redraw(Position from,Dimension size) throws IOException{
		crs.drawColorCharArray(windowPlane.getPartialContent(from, size), from);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends Application> T getApplication(Class<T> app){
		ApplicationFactory fact = appList.locate(app);
		if(fact==null)
			return null;
		T a = (T) fact.createInstance();
		a.begin(this);
		return a;
	}
	
	public void addToAppPool(Class<? extends Application> app, ApplicationFactory factory){
		appList.register(app, factory);
	}
	
	public void submitApplicationToMenu(ApplicationFactory factory){
		rootApplication.addToApplicationMenu(factory);
	}
	
	public void postEvent(Event e){
		evQueue.put(e);
	}

	public void receiveEvent(Event e) {
		if(isRunning()) postEvent(e);		
	}

	public void releaseWindow(Window w) {
		windowPlane.removeChild(w);
	}
	
	public WindowPlane getWindowPlane(){
		return windowPlane;
	}
	
	public Application getCurrentApplication(){
		return currentApp;
	}
	
	public Curses getCurses(){
		return crs;
	}
	
	public void setColorTheme(ColorTheme theme){
		crs.applyColorTheme(theme);
		
		windowPlane.resetColorManager();
		menuPlane.resetColorManager();
		
		windowPlane.refresh();
		menuPlane.refresh();
	}

	public synchronized void terminalResized(int cols, int lines) {
		
		crs.resizeBuffer(cols, lines);
		postEvent(new ResolutionChangeEvent(this, new Dimension(lines,cols)));

	}
	
}
