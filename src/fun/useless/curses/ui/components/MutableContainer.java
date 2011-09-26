package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;

public class MutableContainer<T extends Component> extends Container<T> {

	public MutableContainer(Curses cs,Position p,Dimension d) {
		super(cs,p,d);
	}
	
	public void removeChildren(){
		intRemoveChildren();
	}
	public void removeChild(T c){
		intRemoveChild(c);
	}
	
	public void addChild(T c){
		intAddChild(c);
	}

}
