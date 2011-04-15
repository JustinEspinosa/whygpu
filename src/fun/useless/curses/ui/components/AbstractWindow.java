package fun.useless.curses.ui.components;

import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorPair;
import fun.useless.curses.ui.ColorType;


public abstract class AbstractWindow<T extends Component> extends MutableContainer<T> {
	private String wndTitle;
	
	public AbstractWindow(String title,int sLine,int sCol,int lines,int cols){
		super(sLine,sCol,lines,cols);
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
	
	@Override
	protected void border(){		
		int col;
		
		ColorPair oldcol = getColor();
		setColor(ColorDefaults.getDefaultColor(ColorType.TITLEBAR));
		
		for(col=0;col< getSize().getCols();col++)
			setChar(0,col,'#', true);
			
		int mid = getSize().getCols()/2;
		col = mid - (wndTitle.length()/2);
		
		printAt(0, col-1," " + wndTitle + " ", true);
		
		setColor(oldcol);
		
	}
}
