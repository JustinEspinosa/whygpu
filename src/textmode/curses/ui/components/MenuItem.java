package textmode.curses.ui.components;


import java.util.Vector;

import textmode.curses.Curses;
import textmode.curses.ui.Dimension;
import textmode.curses.ui.Position;
import textmode.curses.ui.event.ActionEvent;
import textmode.curses.ui.event.ActionListener;
import textmode.curses.ui.event.CharacterCodeEvent;
import textmode.curses.ui.event.TermKeyEvent;
import textmode.curses.ui.event.UiEvent;
import textmode.curses.ui.event.UiInputEvent;



public class MenuItem extends Component {

	private String text;
	private PopUp target;
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	private boolean focused = false;
	private int targetL;
	private int targetC;
	
	public MenuItem(String txt,Curses cs) {
		super(cs,new Position(0, 0), new Dimension(1, txt.length()));
		text = txt;
		update();
		notifyDisplayChange();
	}
	
	public void setTargetPosition(int line,int col){
		targetL = line;
		targetC = col;
	}
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				switch(k){
				case TermKeyEvent.RIGHT_ARROW:
				case TermKeyEvent.DOWN_ARROW:
					deployTarget(); break;
				}
			}
			if(uie.getOriginalEvent() instanceof CharacterCodeEvent){
				char c = ((CharacterCodeEvent)uie.getOriginalEvent()).getChar();
				if(c==13)
					action();
			}
		}
		
		super.processEvent(e);
	}
	
	protected void action(){
		ActionEvent e = new ActionEvent(this);
		for(ActionListener a : listeners)
			a.actionPerformed(e);
	}
	
	void insertFirstActionListener(ActionListener a){
		listeners.insertElementAt(a, 0);
	}
	
	public void addActionListener(ActionListener a){
		listeners.add(a);
	}
	
	protected void deployTarget(){
		if(target!=null){
			target.setPosition( new Position(targetL, targetC) );
			target.open();
		}
	}
	protected void closeTarget(){
		if(target!=null)
			target.close();
	}
	public void setTarget(PopUp p){
		target = p;
	}
	public PopUp getTarget(){
		return target;
	}
	
	protected final void update(){
		clear();
		printAt(0, 0, text);
	}
	
	public String getText(){
		return text;
	}

	public void setText(String txt){
		text = txt;
		update();
	}
	@Override
	public synchronized void gotFocus() {
		focused = true;
		setColor(colors().getAlt(getClass()));
		update();
		super.gotFocus();
	}
	
	@Override
	public synchronized void lostFocus() {
		focused = false;
		setColor(colors().get(getClass()));
		update();
		closeTarget();
		super.lostFocus();
	}
	//Deny the use border, takes too much place
	@Override
	public void setBorder(boolean border){
	}

	@Override
	protected synchronized void redraw() {
		if(focused) 
			setColor(colors().getAlt(getClass()));
		else
			setColor(colors().get(getClass()));
		
		update();
	}
}
