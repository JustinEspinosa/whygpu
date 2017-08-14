package com.github.justinespinosa.textmode.curses.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.TerminalResizedReceiver;
import com.github.justinespinosa.textmode.curses.application.Application;
import com.github.justinespinosa.textmode.curses.application.ApplicationFactory;
import com.github.justinespinosa.textmode.curses.application.FactoryLocator;
import com.github.justinespinosa.textmode.curses.application.RootApplication;
import com.github.justinespinosa.textmode.curses.lang.ColorChar;
import com.github.justinespinosa.textmode.curses.term.Terminal;
import com.github.justinespinosa.textmode.curses.ui.components.Component;
import com.github.justinespinosa.textmode.curses.ui.components.MenuBar;
import com.github.justinespinosa.textmode.curses.ui.components.MenuPlane;
import com.github.justinespinosa.textmode.curses.ui.components.PopUp;
import com.github.justinespinosa.textmode.curses.ui.components.RootPlane;
import com.github.justinespinosa.textmode.curses.ui.components.Window;
import com.github.justinespinosa.textmode.curses.ui.components.WindowPlane;
import com.github.justinespinosa.textmode.curses.ui.event.CharacterCodeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.CursorControlEvent;
import com.github.justinespinosa.textmode.curses.ui.event.Event;
import com.github.justinespinosa.textmode.curses.ui.event.EventReceiver;
import com.github.justinespinosa.textmode.curses.ui.event.RedrawEvent;
import com.github.justinespinosa.textmode.curses.ui.event.ResolutionChangeEvent;
import com.github.justinespinosa.textmode.curses.ui.event.StopUIThread;
import com.github.justinespinosa.textmode.curses.ui.event.TermKeyEvent;
import com.github.justinespinosa.textmode.curses.ui.event.TerminalInputEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiInputEvent;
import com.github.justinespinosa.textmode.curses.ui.look.ColorTheme;
import com.github.justinespinosa.textmode.util.SuspendableThread;
import com.github.justinespinosa.textmode.xfer.ZModem;



public class WindowManager implements EventReceiver, TerminalResizedReceiver {
	
	private final class EventPoster extends Thread{
		private Event event;
		public EventPoster(Event e){
			event = e;
		}
		public void run(){
			evQueue.put(event);
		}
	}
	
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
		private final ArrayBlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(200);

		private EventQueue() { }
		/**
		 * Put a new event
		 * @param e
		 * @throws  
		 */
		private void put(Event e)  {
			try{
				queue.put(e);
			}catch(InterruptedException ie){}
		}

		/**
		 * Get the current event and removing (making next event current)
		 * @return
		 */
		private Event pop() throws InterruptedException{
			return queue.take();
		}
	}
	
	private static class Driver extends SuspendableThread{
		private AtomicBoolean running = new AtomicBoolean(false);
		
		public Driver(ThreadGroup thg, String n) {
			super(thg,n);
		}
		
		@Override
		public synchronized void start() {
			running.set(true);
			super.start();
		}
		
		public boolean isRunning(){
			return running.get();
		}
		
		public void stopAsap(){
			running.set(false);
		}
	}
	
	private final class InputDriver extends Driver{
		
		private InputDriver(ThreadGroup thg) { 
			super(thg,"Read");
			setDaemon(true);
		}
		
		@Override
		public void run(){
			try{
				while(isRunning()){

					pauseIfNeeded();

					//terminal.acquireRead();

					if(isRunning()){		
						try{
							int b = termInput.read();						
							if(b == 0xffffffff){
								termInput.close();
								throw new IOException("end of stream");
							}
									
							eventer.put(b);
						}catch(IOException e){
							if(!(e.getCause() instanceof InterruptedException))
								throw e;
						}
					}

					//terminal.releaseRead();

				}
					
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				WindowManager.this.stop();
			}
				
		}
	}

	
	private final class OutputDriver extends Driver{
		private RectangleBuffer buffer = new RectangleBuffer();
		
		private OutputDriver(ThreadGroup thg) { 
			super(thg,"Write");
			setDaemon(true);
		}
		
		public void addRect(Rectangle r){
			buffer.addToArea(r);
		}
		
		@Override
		public void run(){
			try{
				while(isRunning()){
					
					pauseIfNeeded();
					
					sleep(50);
					
					pauseIfNeeded();
					
					Rectangle[] rArray = buffer.getArea();
					
					pauseIfNeeded();
					
					//terminal.acquireWrite();
					
					if(isRunning()){
						for(Rectangle r : rArray)
							redraw(r);
					}
					
					//terminal.releaseWrite();
					
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				WindowManager.this.stop();
			}
				

		}
	}

	private final class EventDispatcher extends Driver{
		
		private EventDispatcher(ThreadGroup thg) { 
			super(thg,"EventDispatcher");
			setDaemon(true);
		}
		
		@Override
		public void run(){
			while(isRunning()){
				try{
					Event ev = evQueue.pop();
					if(ev!=null){
						if(ev instanceof StopUIThread)
							stopAsap();
						else
							processEvent(ev);
					}

				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	}
	
	private UiEventProcessorFactory processorFactory = new UiEventProcessorFactory() {
		public UiEventProcessor createProcessor(UiEvent e, RootPlane<?> plane) {
			return new UiEventProcessor(e,plane);
		}
	};

	private Vector<Application>  runningApps = new Vector<Application>();
	private EventQueue           evQueue     = new EventQueue();
	private TerminalInputEventer eventer     = new TerminalInputEventer();
	private EscapeDetector       detector    = new EscapeDetector();
	private FactoryLocator       appList     = new FactoryLocator();
	private RootApplication      rootApplication;
	private Application          currentApp;
	private RootPlane<?>         activePlane;
	private InputStream          termInput;
	private WindowPlane          windowPlane;
	private MenuPlane            menuPlane;
	private Curses crs;
	private Terminal             terminal;
	private EventDispatcher      dispatcher;
	private OutputDriver         output;
	private InputDriver          input;
	private Thread               caller;
	private AtomicBoolean        isstopping = new AtomicBoolean(false);
	
	
	public WindowManager(Curses c) throws IOException{
		this(c,null);
	}
	
	public WindowManager(Curses c, RootApplication root) throws IOException{
		crs = c;
		terminal = crs.getTerminal();
		menuPlane       = new MenuPlane(this,crs,new Position(0, 0),new Dimension(crs.lines(), crs.cols()));
		windowPlane     = new WindowPlane(this,crs,new Position(0,0),new Dimension(crs.lines(),crs.cols()));
		
		if(root==null)
			root = new RootApplication();
		
		rootApplication = root;
		activePlane     = windowPlane;
		termInput       = terminal.getInputStream();
		
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

	
	public void suspend(){

		try {
			
			input.pause();
			terminal.wakeup();
			//terminal.acquireRead();
			
			
			output.pause();
			terminal.wakeup();
			//terminal.acquireWrite();
			
			crs.rmcup();
			crs.clear();
			
			crs.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		//}catch (InterruptedException e){
		}
		
	}
	
	private void initScreen() throws IOException{
		crs.smcup();
		crs.invalidateDoubleBuffering();
		crs.showWindow(windowPlane);
		crs.flush();
	}
		
	public void resume(){
		
		try {
			
			initScreen();
		
			//terminal.releaseRead();
			//terminal.releaseWrite();
			
			output.unpause();
			input.unpause();
		
		//} catch (InterruptedException e){
		} catch (IOException e){
			
		}
		
		
	}
	
	public ZModem createZModem(){
		return new ZModem(terminal.getInputStream(),terminal.getOutputStream());
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
				output.addRect(r);

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
	public void stop(){

		if(!isstopping.compareAndSet(false, true))
			return;
		
		try {
			
			postEvent(new StopUIThread(this));
			
			input.stopAsap();
			output.stopAsap();
			
			terminal.wakeup();
			//terminal.acquireRead();			
			
			terminal.wakeup();
			//terminal.acquireWrite();

			crs.rmcup();
			crs.clear();
			crs.cnorm();
			
			crs.flush();
	
			//terminal.releaseRead();
			//terminal.releaseWrite();
	
			terminal.closeChannel();
			
		}catch (IOException e) {
		//}catch (InterruptedException e1) {
		}finally{
			LockSupport.unpark(this.caller);
		}
	}

	
	private synchronized void begin(Thread caller){
		this.caller = caller;
		dispatcher  = new EventDispatcher(caller.getThreadGroup());      
		output      = new OutputDriver(caller.getThreadGroup());  
		input       = new InputDriver(caller.getThreadGroup());
	}
	
	public void start() throws IOException{
		if(this.caller!=null)
			return;
		
		begin(Thread.currentThread());
		
		initScreen();
		
		dispatcher.start();
		input.start();
		output.start();
		
		rootApplication.begin(this);
		
		while(input.isRunning())
			LockSupport.park();
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
		(new EventPoster(e)).start();
	}

	public void receiveEvent(Event e) {
		if(this.caller!=null)
			postEvent(e);		
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
