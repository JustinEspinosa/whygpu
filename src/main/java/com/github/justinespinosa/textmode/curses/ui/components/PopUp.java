package com.github.justinespinosa.textmode.curses.ui.components;

import java.util.Enumeration;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.WindowManager;
import com.github.justinespinosa.textmode.curses.ui.event.ActionEvent;
import com.github.justinespinosa.textmode.curses.ui.event.TermKeyEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiEvent;
import com.github.justinespinosa.textmode.curses.ui.event.UiInputEvent;


public class PopUp extends AbstractMenu {

	private int maxCols = 30;
	
	public PopUp(Curses cs, int cols, WindowManager m) {
		super(cs,new Dimension(0,1),m);
		maxCols = cols;
		setVisible(false);
	}
	
	public void addItem(MenuItem m){
		
		m.setPosition( new Position(getSize().getLines(), 0) );

		setSize(getSize().down());
		
		intAddChild(m);
		
		adaptPopUpWidth(m.getText().length());
		
		if(selectedIndex==-1)
			selectedIndex = 0;
	}
	
	@Override
	public void addChild(MenuItem c) {
		addItem(c);
	}

	public void open(){
		setVisible(true);
		gotFocus();
	}
	
	public void close(){
		setVisible(false);
		lostFocus();
	}
	
	private void adaptPopUpWidth(int width){
		
		if(width>maxCols)
			width=maxCols;
		
		if(width>getSize().getCols()){
			setSize(getSize().horizontal(width - getSize().getCols()));
		}
		
		Enumeration<MenuItem> children = children();
		while(children.hasMoreElements()){
			MenuItem child = children.nextElement();
			
			if(child.getSize().getCols()!=getSize().getCols())
				child.setSize( new Dimension(1,getSize().getCols()) );
		}
	}
	
	@Override
	public void processEvent(UiEvent e) {
		if(e instanceof UiInputEvent){
			UiInputEvent uie = (UiInputEvent) e;
			if(uie.getOriginalEvent() instanceof TermKeyEvent){
				int k = ((TermKeyEvent)uie.getOriginalEvent()).getKey();
				if(k == TermKeyEvent.DOWN_ARROW){
					nextItem();
					return;
				}
				if(k == TermKeyEvent.UP_ARROW){
					previousItem();
					return;
				}
				if(k == TermKeyEvent.CANCEL){
					close();
					return;
				}
			}
		}
		
		super.processEvent(e);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(getSelectedItem()!=null)
			if(getSelectedItem().getTarget()==null)
				close();
			
		super.actionPerformed(e);
	}

	@Override
	protected synchronized void redraw() {
		clear();
	}
}
