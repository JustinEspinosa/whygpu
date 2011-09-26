package fun.useless.curses.ui;

public class Position {
	private int col;
	private int line;
	public Position(int l,int c){
		col = c;
		line = l;
	}
	public int getCol(){
		return col;
	}
	public int getLine(){
		return line;
	}
	@Override
	public String toString() {
		return "Position("+line+";"+col+")";
	}
	
	public Position horizontal(int diff){
		return new Position(line,col+diff);
	}
	public Position vertical(int diff){
		return new Position(line+diff,col);
	}
	public Position right(){
		return new Position(line,col+1);
	}
	public Position left(){
		return new Position(line,col-1);
	}
	public Position up(){
		return new Position(line-1,col);
	}
	public Position down(){
		return new Position(line+1,col);
	}
	public Position copy(){
		return new Position(line,col);
	}
}
