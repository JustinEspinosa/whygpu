package textmode.curses.ui;

public class Position {
	
	public static final Position ORIGIN = new Position(0,0);
	
	private int col;
	private int line;
	public Position(int l,int c){
		col = c;
		line = l;
	}
	
	public <T> T getAt(T[][] arr){
		return arr[line][col];
	}
	public <T> void setAt(T[][] arr, T v){
		arr[line][col] = v;
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
	
	public Position relativeTo(Position p){
		return translate(-p.line,-p.col);
	}
	
	public Position translate(int vdiff,int hdiff){
		return new Position(line+vdiff,col+hdiff);
	}
	
	public Position withNewCol(int newcol){
		return new Position(line,newcol);
	}
	
	public Position horizontal(int diff){
		return new Position(line,col+diff);
	}
	
	public Position withNewLine(int newline){
		return new Position(newline,col);
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
	
	public boolean sameCol(Position pos){
		return pos.col==col;
	}
	
	public boolean sameLine(Position pos){
		return pos.line==line;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Position)
			return ((Position)obj).line==line && ((Position)obj).col==col;
		
		return false;
	}

	public Position add(Position p) {
		return new Position(line+p.line,col+p.col);
	}
	
	public Position subtract(Position p) {
		return new Position(line-p.line,col-p.col);
	}
}
