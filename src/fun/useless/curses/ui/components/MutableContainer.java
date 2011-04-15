package fun.useless.curses.ui.components;

public class MutableContainer<T extends Component> extends Container<T> {

	public MutableContainer(int sLine, int sCol, int lines, int cols) {
		super(sLine, sCol, lines, cols);
	}
	
	public void removeChild(T c){
		intRemoveChild(c);
	}
	public void addChild(T c){
		intAddChild(c);
	}

}
