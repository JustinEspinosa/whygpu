package textmode.curses.ui;

public class Rectangle {
	private Position pos;
	private Dimension dim;
	
	
	public Rectangle(Position p1,Position p2){
		pos = new Position( Math.min(p1.getLine(), p2.getLine()), Math.min(p1.getCol(), p2.getCol()) );
		dim = new Dimension( p2.getLine() - p1.getLine() , p2.getCol() - p1.getCol() );
	}
	
	public Rectangle(Position p,Dimension d){
		pos = p.copy();
		dim = d.copy();
	}
	
	public Rectangle(int line,int col,int lines,int cols){
		pos = new Position(line,col);
		dim = new Dimension(lines,cols);
	}
	
	@Override
	public String toString() {
		return "Rectangle["+pos+","+dim+"]";
	}

	/*shortcuts*/
	public int getCol(){ return pos.getCol(); }
	public int getLine(){ return pos.getLine(); }
	public int getCols() { return dim.getCols();  }
	public int getLines(){ return dim.getLines(); }
	
	public Position getPosition() { return pos.copy();  }
	public Dimension getDimension(){ return dim.copy(); }
	
	/* absolute */
	public Position getOrigin() { return getPosition();  }
	public Position getEnd() { return new Position(pos.getLine()+dim.getLines(),pos.getCol()+dim.getCols()); }
	
	private boolean includesVertically(Rectangle r){
		return (r.getLine() >= getLine() && r.getLine()+r.getLines() <= getLine()+getLines());
	}
	
	private boolean includesHorizontally(Rectangle r){
		return (r.getCol() >= getCol() && r.getCol()+r.getCols() <= getCol()+getCols());
	}
	
	public boolean includes(Rectangle r){
		return includesVertically(r) && includesHorizontally(r);
	}
	
	public Rectangle moveTo(Position p){
		return new Rectangle(p, dim);
	}
	
	public Rectangle moveOf(Position p){
		return new Rectangle(pos.add(p), dim);		
	}
	
}
