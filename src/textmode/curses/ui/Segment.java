package textmode.curses.ui;

public class Segment {
	
	public static Segment[] slice(Rectangle r){
		Segment[] retval = new Segment[r.getLines()];
		for(int i=0;i<retval.length;++i)
			retval[i] = new Segment(r.getPosition().vertical(i),r.getCols());
		
		return retval;
	}
	
	private Position pos;
	private int length;

	public Segment(Position p,int len){
		pos = p.copy();
		length = len;
	}
	
	@Override
	public String toString() {
		return "Segment["+pos+","+length+"]";
	}
	
	public Segment copy(){
		return new Segment(pos,length);
	}
	
	public Rectangle toRectangle(){
		return new Rectangle(pos,getDimension());
	}
	
	/**
	 * If this and s are overlaped or contiguous, then this changes to a merge of this and s
	 * @param s
	 * @return true if something was done
	 */
	public boolean absorb(Segment s){
		if(!pos.sameLine(s.pos))
			return false;
		
		if(  s.pos.getCol() + s.length >= ( pos.getCol()-1)  
		   &&  pos.getCol() +   length >= (s.pos.getCol()-1) ){
			pos = pos.withNewCol(Math.min(pos.getCol(), s.pos.getCol()));
			length = Math.max(s.pos.getCol() + s.length,  pos.getCol() +   length) - pos.getCol();
			
		}
		
		return false;
	}

	/*shortcuts*/
	public int getCol(){ return pos.getCol(); }
	public int getLine(){ return pos.getLine(); }
	
	public Position getPosition() { return pos.copy();  }
	public Dimension getDimension(){ return new Dimension(1,length); }
	
	public int getLength(){ return length; }
	public int length(){ return getLength(); }
	public int getCols(){ return getLength(); }


}
