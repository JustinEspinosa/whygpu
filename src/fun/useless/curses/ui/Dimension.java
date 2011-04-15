package fun.useless.curses.ui;

public class Dimension {
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
}
