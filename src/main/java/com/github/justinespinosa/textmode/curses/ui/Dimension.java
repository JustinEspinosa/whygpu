package com.github.justinespinosa.textmode.curses.ui;

import java.lang.reflect.Array;
import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Dimension implements Iterable<Position>{
	
	private class PositionIterator implements Iterator<Position>{
		private Position corner;
		private Position current;
		private Position currentLine;

		public PositionIterator(Position start){
			corner      = start.copy();
			current     = start.copy();
			currentLine = start.copy();
		}
		
		public PositionIterator(){
			this(Position.ORIGIN);
		}
		
		public boolean hasNext() {
			return includes(current.subtract(corner));
		}

		public Position next() {
			Position retPos = current;
			
			current = current.right();
			
			if(!includes(current.subtract(corner))){
				currentLine = currentLine.down();
				current = currentLine.copy();
			}

			if(includes(retPos.subtract(corner)))
				return retPos;
			else
				return null;
		}

		public void remove() {
			throw new NotImplementedException();
		}
		
	}
	
	public static final Dimension UNITY = new Dimension(1, 1);
	
	private long nLines;
	private long nCols;
	/**
	 * The dimensions are abs()ized.
	 * @param lines
	 * @param cols
	 */
	public Dimension(int lines,int cols){
		this((long)lines,(long)cols);
	}
	
	public Dimension(long lines, long cols) {
		nLines = Math.abs(lines);
		nCols  = Math.abs(cols);
	}

	public int getCols(){
		return (int)nCols;
	}
	public int getLines(){
		return (int)nLines;
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
	
	public Dimension keepLines(int cols){
		return new Dimension(nLines,cols);
	}
	public Dimension keepCols(int lines){
		return new Dimension(lines,nCols);
	}
	
	public Dimension scale(double mult){
		return new Dimension((long)(mult*(double)nLines),(long)(mult*(double)nCols));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Dimension)
			return ((Dimension)obj).nLines==nLines && ((Dimension)obj).nCols==nCols;
		
		return false;
	}
	
	public Iterator<Position> iterator(Position start){
		return new PositionIterator(start);
	}

	@Override
	public Iterator<Position> iterator(){
		return new PositionIterator();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[][] newArrayOf(Class<T> type){
		return (T[][]) Array.newInstance(type,(int)nLines,(int)nCols); 
	}
}
