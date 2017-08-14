package com.github.justinespinosa.textmode.curses.ui.components;

import com.github.justinespinosa.textmode.curses.Curses;
import com.github.justinespinosa.textmode.curses.ui.ColorPair;
import com.github.justinespinosa.textmode.curses.ui.Dimension;
import com.github.justinespinosa.textmode.curses.ui.Position;
import com.github.justinespinosa.textmode.curses.ui.look.ColorManager;


public abstract class AbstractWindow<T extends Component> extends MutableContainer<T> {
	private String wndTitle;
	private boolean decorated = true;
	
	public AbstractWindow(String title, Curses cs, Position p, Dimension d){
		super(cs,p,d);
		wndTitle=title;
	}
	
	protected void setDecorated(boolean v){
		decorated = v;
	}
	
	@Override
	protected  int getInnerRight(){
		return getSize().getCols()-1;
	}
	
	@Override
	protected  int getInnerBottom(){
		return getSize().getLines()-1;
	}
	
	@Override
	protected  int getInnerLeft(){
		return 0;
	}
	
	protected abstract boolean isActive();
	
	protected synchronized void setTitle(String title){
		wndTitle = title;
		border();
	}
	
	private void drawDecoration(){
		int col;
		
		if( isActive() )
			setColor(colors().getCustom(getClass(), ColorManager.TITLEBAR));
		else
			setColor(colors().getCustom(getClass(), ColorManager.TITLEBAR_GRAYED));
		
		for(col=0;col< getSize().getCols();col++)
			setChar(0,col,'#', true);
			
		int mid = getSize().getCols()/2;
		col = mid - (wndTitle.length()/2);
		
		printAt(0, col-1," " + wndTitle + " ", true);
	}
	
	
	@Override
	protected void border(){		
		
		if(!decorated){
			super.border();
			return;
		}
		
		ColorPair oldcol = getColor();

		drawDecoration();
		
		setColor(oldcol);
		
	}
}
