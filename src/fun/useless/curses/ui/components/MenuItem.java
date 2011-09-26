package fun.useless.curses.ui.components;


import java.util.Vector;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;
import fun.useless.curses.ui.event.CharacterCodeEvent;
import fun.useless.curses.ui.event.TermKeyEvent;
import fun.useless.curses.ui.event.UiEvent;
import fun.useless.curses.ui.event.UiInputEvent;


public class MenuItem extends Component {

	private String text;
	private PopUp target;
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	private int targetL;
	private int targetC;
	
	public MenuItem(String txt,Curses cs) {
		super(cs,new Position(0, 0), new Dimension(1, txt.length()+1));
		text = txt;
		setColor(ColorDefaults.getDefaultColor(ColorType.MENU,curses()));
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
	

	@Override
	public void setColor(ColorPair p){
		super.setColor(p);
		update();
	}	
	
	public String getText(){
		return text;
	}
	
	public void setText(String txt){
		text = txt;
		update();
	}
	@Override
	public void gotFocus() {
		setColor(ColorDefaults.getDefaultColor(ColorType.SELECTED,curses()));
		super.gotFocus();
	}
	@Override
	public void lostFocus() {
		setColor(ColorDefaults.getDefaultColor(ColorType.MENU,curses()));
		closeTarget();
		super.lostFocus();
	}
	//Disallow border, takes too much place
	@Override
	public void setBorder(boolean border){
	}
}
