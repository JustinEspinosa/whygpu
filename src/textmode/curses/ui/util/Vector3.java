package textmode.curses.ui.util;

public class Vector3 {
	private int _x;
	private int _y;
	private int _z;
	
	public Vector3(int x,int y,int z) {
		_x=x; _y=y; _z=z;
	}
	public Vector3(Vector3 a,Vector3 b){
		_x=a._x-b._x;
		_y=a._y-b._y;
		_z=a._z-b._z;
	}
	
	public double norm(){
		return Math.sqrt((double)(_x*_x+_y*_y+_z*_z));
	}

	public double distance(Vector3 v){
		 Vector3 v2 = new Vector3(this, v);
		 return v2.norm();
	}
	
	
	public int x() {
		return _x;
	}
	public void setX(int _x) {
		this._x = _x;
	}
	public int y() {
		return _y;
	}
	public void setY(int _y) {
		this._y = _y;
	}
	public int z() {
		return _z;
	}
	public void setZ(int _z) {
		this._z = _z;
	}

}
