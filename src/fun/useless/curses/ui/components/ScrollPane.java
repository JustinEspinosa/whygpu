package fun.useless.curses.ui.components;

import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.Event;
import fun.useless.curses.ui.event.EventReceiver;
import fun.useless.curses.ui.event.RedrawEvent;
import fun.useless.curses.ui.event.SizeChangeListener;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;

public class ScrollPane<T extends Component> extends Container<Component> implements SizeChangeListener<Component>, EventReceiver{
	protected final static int limitLR(int max,int min, int num){
		return Math.min(Math.max(min,num), max);
	}
	protected final static int getScrollOffset(int posToShow,int controlPos, int controlSize, int mySize){

		int maxControlPos = 0;
		int minControlPos = (mySize - 2) - (controlSize-1);
		int viewPartRelativePos = posToShow + controlPos;
		
		if( viewPartRelativePos < 1)
			return limitLR(maxControlPos,minControlPos,1 - posToShow);
		
		if( viewPartRelativePos > (mySize - 3) )
			return limitLR(maxControlPos,minControlPos,(mySize - 3) - posToShow);
		
		return controlPos;
	}
	
	private T innerComponent;
	private ScrollBar vert;
	private ScrollBar horz;
	private Button    corner;
	private boolean scrollMode = false;
	
	public ScrollPane(T component,int sLine, int sCol, int lines, int cols) {
		super(sLine, sCol, lines, cols);
		
		innerComponent=component;
		intAddChild(innerComponent);
		disableChildrenEventSending();
		setFocusNoNotify(innerComponent);
		
		createComponents();
		computeMaxes();
		positionGrips(-1);
		
		innerComponent.addSizeChangeListener(this);
		
		notifyDisplayChange();
	}
	
	public boolean isScrollLock(){
		return scrollMode;
	}
	
	private void createComponents(){
		vert = new ScrollBar(ScrollBar.VERTICAL, getInnerTop(), getInnerRight(), getSize().getLines()-1);
		horz = new ScrollBar(ScrollBar.HORIZONTAL, getInnerBottom(),getInnerLeft(),getSize().getCols()-1);
		corner = new Button(" ",getInnerBottom(),getInnerRight(),1,1);
		intAddChild(vert);
		intAddChild(horz);
		intAddChild(corner);
	}
	
	@Override
	protected void userResized(){
		vert.setPosition(new Position(getInnerTop(), getInnerRight()));
		vert.setSize(new Dimension(getSize().getLines()-1, 1));
		
		horz.setPosition(new Position(getInnerBottom(), getInnerLeft()));
		horz.setSize(new Dimension(1, getSize().getCols()-1));
		
		corner.setPosition(new Position(getInnerBottom(),getInnerRight()));
		
		computeMaxes();
		positionGrips(-1);
	}
	
	public T getComponent(){
		return innerComponent;
	}
	
	private int componentEndCol(){
		return innerComponent.getPosition().getCol() + innerComponent.getSize().getCols();		
	}
	
	private int componentEndLine(){
		return innerComponent.getPosition().getLine() + innerComponent.getSize().getLines();
	}
	
	private boolean canScroll(int k)
	{
		switch( k ){
		case TermKeyEvent.LEFT_ARROW:
			return innerComponent.getPosition().getCol() < 0;
		case TermKeyEvent.RIGHT_ARROW:
			return componentEndCol() > getSize().getCols() - 1;
		case TermKeyEvent.UP_ARROW:
			return innerComponent.getPosition().getLine() < 0;
		case TermKeyEvent.DOWN_ARROW:
			return componentEndLine() > getSize().getLines() - 1;
		}
		return false;
	}
	private void doScroll(int k){
		switch( k ){
		case TermKeyEvent.LEFT_ARROW:
			innerComponent.setPosition(innerComponent.getPosition().right()); return;
		case TermKeyEvent.RIGHT_ARROW:
			innerComponent.setPosition(innerComponent.getPosition().left());  return;
		case TermKeyEvent.UP_ARROW:
			innerComponent.setPosition(innerComponent.getPosition().down());  return;
		case TermKeyEvent.DOWN_ARROW:
			innerComponent.setPosition(innerComponent.getPosition().up());    return;
		}
	}
	
	private void positionVerticalGrip(int button){
		int start = 0 - innerComponent.getPosition().getLine();
		int end   = start + (getSize().getLines() -1);
		vert.setViewPort(start, end, button);
	}
	private void positionHorizontalGrip(int button){
		int start = 0 - innerComponent.getPosition().getCol();
		int end   = start + (getSize().getCols() -1);
		horz.setViewPort(start, end, button);
	}	
	private void positionGrips(int k){
		int buttonV = -1;
		int buttonH = -1;
		switch(k){
		case TermKeyEvent.LEFT_ARROW:
			buttonH = ScrollBar.PREVIOUS;
			break;
		case TermKeyEvent.UP_ARROW:
			buttonV = ScrollBar.PREVIOUS;
			break;
		case TermKeyEvent.RIGHT_ARROW:
			buttonH = ScrollBar.NEXT;
			break;
		case TermKeyEvent.DOWN_ARROW:
			buttonV = ScrollBar.NEXT;
			break;
		}
		
		positionVerticalGrip(buttonV);
		positionHorizontalGrip(buttonH);
	}
	
	private void scroll(int k){
	
		if(canScroll(k)){
			doScroll(k);
			positionGrips(k);
			notifyDisplayChange();
		}
	}

	
	public void scrollToView(Position p){
		int line = getScrollOffset(p.getLine(),innerComponent.getPosition().getLine(),innerComponent.getSize().getLines(),getSize().getLines());
		int col  = getScrollOffset(p.getCol() ,innerComponent.getPosition().getCol() ,innerComponent.getSize().getCols() ,getSize().getCols() );
		innerComponent.setPosition(new Position(line, col));
		positionGrips(-1);
		notifyDisplayChange();
	}
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent() ).getKey();
				switch( k ){
				case TermKeyEvent.LEFT_ARROW:
				case TermKeyEvent.RIGHT_ARROW:
				case TermKeyEvent.UP_ARROW:
				case TermKeyEvent.DOWN_ARROW:
					if(scrollMode){
						scroll(k); 
						return;
					}
					break;
				case TermKeyEvent.SCROLL:
					scrollMode = !scrollMode; 
					return;
				}
			}
		}
		super.processEvent(e);
	}
	private void disableChildrenEventSending(){
		innerComponent.setEventReceiver(this);
	}
	
	@Override
	protected void setEventReceiver(EventReceiver l) {
		super.setEventReceiver(l);
		disableChildrenEventSending();
	}
	
	private void computeMaxes(){
		horz.setMax(Math.max(getSize().getCols()-1,innerComponent.getSize().getCols()));
		vert.setMax(Math.max(getSize().getLines()-1,innerComponent.getSize().getLines()));
		notifyDisplayChange();
	}
	


	@Override
	public void sizeChanged(Component src) {
		if(src==innerComponent)
			computeMaxes();
	}

	@Override
	public void receiveEvent(Event e) {
		if(e instanceof RedrawEvent){
			notifyDisplayChange();
		}else{
			sendEvent(e);
		}
	}

}
