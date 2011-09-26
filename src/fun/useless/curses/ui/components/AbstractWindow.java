package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;


public abstract class AbstractWindow<T extends Component> extends MutableContainer<T> {
	private String wndTitle;
	
	public AbstractWindow(String title,Curses cs,Position p,Dimension d){
		super(cs,p,d);
		wndTitle=title;
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
	
	
	private void drawDecoration(){
		int col;
		
		if( isActive() )
			setColor(ColorDefaults.getDefaultColor(ColorType.TITLEBAR,curses()));
		else
			setColor(ColorDefaults.getDefaultColor(ColorType.GREYED,curses()));
		
		for(col=0;col< getSize().getCols();col++)
			setChar(0,col,'#', true);
			
		int mid = getSize().getCols()/2;
		col = mid - (wndTitle.length()/2);
		
		printAt(0, col-1," " + wndTitle + " ", true);
	}
	
	
	@Override
	protected void border(){		
		
		ColorPair oldcol = getColor();

		drawDecoration();
		
		setColor(oldcol);
		
	}
}
