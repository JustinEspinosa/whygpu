package fun.useless.curses.ui.components;

import fun.useless.curses.ui.ColorDefaults;
import fun.useless.curses.ui.ColorType;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

/**
 * This scrollbar is only the graphic. the behavior is handeld by a ScrollPane.
 * @author justin
 *
 */
public class ScrollBar extends Container<Button> {
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;

	public static final int PREVIOUS = 0;
	public static final int NEXT = 1;
	
	private int scrollType;
	private Button prev;
	private Button next;
	private Button grip;
	private float positionStart = 0;
	private float positionEnd = 1f;
	private int userMax = 100;
	private int userViewStart = 0;
	private int userViewEnd = 100;
	private int scrEnd;
	
	public ScrollBar(int type,int sLine,int sCol,int len) {
		super(sLine, sCol, type==VERTICAL?len:1, type==HORIZONTAL?len:1);
		setMinSize(type==VERTICAL?-1:1,type==HORIZONTAL?-1:1);
		scrollType = type;
		setColor(ColorDefaults.getDefaultColor(ColorType.WINDOW));
		scrEnd = len -1;
		clear();
		createControls();
		notifyDisplayChange();
	}

	private float getInnerLenF(){
		return scrEnd-1;
	}
	private int getHEnd(){
		return (scrollType==HORIZONTAL?scrEnd:0);
	}
	private int getVEnd(){
		return (scrollType==VERTICAL?scrEnd:0);
	}
	private int gripSLine(){
		if(scrollType==HORIZONTAL) return 0;
		return (int)((float)(getInnerLenF()*positionStart))+1;
	}
	private int gripSCol(){
		if(scrollType==VERTICAL)   return 0;
		return (int)((float)(getInnerLenF()*positionStart))+1;
	}
	private int gripLines(){
		if(scrollType==HORIZONTAL) return 1;
		return Math.max(1,(int)((float)(getInnerLenF()*positionEnd)));
	}
	private int gripCols(){
		if(scrollType==VERTICAL)   return 1;
		return Math.max(1,(int)((float)(getInnerLenF()*positionEnd)));
	}
	private String nextText(){
		return (scrollType==HORIZONTAL?">":"v");
	}
	private String prevText(){
		return (scrollType==HORIZONTAL?"<":"^");
	}
	
	private void createControls(){
		next = new Button(nextText(),getVEnd(),getHEnd(),1);
		prev = new Button(prevText(),0,0,1);
		grip = new Button(" ",gripSLine(),gripSCol(),gripLines(),gripCols());
		
		intAddChild(grip);
		intAddChild(next);
		intAddChild(prev);
	}
	
	@Override
	protected void userResized(){
		int len = 1;
		if(scrollType==VERTICAL)
			len = getSize().getLines();
		if(scrollType==HORIZONTAL)
			len = getSize().getCols();
		
		scrEnd = len - 1;
		next.setPosition(new Position(getVEnd(),getHEnd()));
		computePositions();
		reSizeGrip();
	}
	
	private void reSizeGrip(){
		Position p = new Position(gripSLine() , gripSCol() );
		grip.setPosition(p);
		Dimension d = new Dimension(gripLines(),gripCols() );
		grip.setSize(d);
	}
	
	private void computePositions(){
		positionStart = (float) ((float)userViewStart/(float)userMax);
		positionEnd   = (float) ((float)userViewEnd/(float)userMax);
	}
	
	public void setViewPort(int start, int end, int button){
		if(button==NEXT) next.push();
		if(button==PREVIOUS) prev.push();
		setViewPort(start,end);
	}
	public void setViewPort(int start, int end){
		userViewStart = start;
		userViewEnd   = end;
		computePositions();
		reSizeGrip();
	}
	
	public void setMax(int m){
		userMax = m;
		computePositions();
		reSizeGrip();
	}


}
