package textmode.curses.ui;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Dimension {
	
	private class PositionIterator implements Iterator<Position>{
		private Position current;
		private Position currentLine;

		public PositionIterator(){
			current = Position.ORIGIN.copy();
			currentLine = Position.ORIGIN.copy();
		}
		
		public boolean hasNext() {
			return includes(current);
		}

		public Position next() {
			Position retPos = current;
			
			current = current.right();
			
			if(!includes(current)){
				currentLine = currentLine.down();
				current = currentLine.copy();
			}

			if(includes(retPos))
				return retPos;
			else
				return null;
		}

		public void remove() {
			throw new NotImplementedException();
		}
		
	}
	
	public static final Dimension UNITY = new Dimension(1, 1);
	
	private int nLines;
	private int nCols;
	/**
	 * The dimensions are abs()ized.
	 * @param lines
	 * @param cols
	 */
	public Dimension(int lines,int cols){
		nLines = Math.abs(lines);
		nCols  = Math.abs(cols);
	}
	
	public int getCols(){
		return nCols;
	}
	public int getLines(){
		return nLines;
	}
	
	@Override
	public String toString() {
		return "Dimension("+nLines+";"+nCols+")";
	}
	
	public boolean includes(Position p){
		return (p.getLine()>=0 && p.getCol()>=0 && 
				p.getLine()<getLines() && p.getCol()<getCols());
	}
	public Dimension mutate(int vDiff,int hDiff){
		return new Dimension(nLines+vDiff,nCols+hDiff);
	}
	public Dimension horizontal(int diff){
		return new Dimension(nLines,nCols+diff);
	}
	public Dimension vertical(int diff){
		return new Dimension(nLines+diff,nCols);
	}
	public Dimension right(){
		return new Dimension(nLines,nCols+1);
	}
	public Dimension left(){
		return new Dimension(nLines,nCols-1);
	}
	public Dimension up(){
		return new Dimension(nLines-1,nCols);
	}
	public Dimension down(){
		return new Dimension(nLines+1,nCols);
	}
	public Dimension copy(){
		return new Dimension(nLines,nCols);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Dimension)
			return ((Dimension)obj).nLines==nLines && ((Dimension)obj).nCols==nCols;
		
		return false;
	}
	
	public Iterator<Position> iterator(){
		return new PositionIterator();
	}
}
