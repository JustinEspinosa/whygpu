package com.github.justinespinosa.textmode.curses.ui.util;

public class Vector3 {
	private int _x;
	private int _y;
	private int _z;
	
	/**
	 * The storage is int. This constructor converts with Math.round();
	 */
	public Vector3(double x,double y,double z) {
		this(	Math.round(x),
				Math.round(y),
				Math.round(z));
	}
	
	/**
	 * The storage is int. This constructor casts to int
	 */
	public Vector3(long x,long y,long z) {
		this(	(int)x,
				(int)y,
				(int)z);
	}
	
	public Vector3(int x,int y,int z) {
		_x=x; _y=y; _z=z;
	}
	
	/**
	 * Constructs the difference
	 * @param a
	 * @param b
	 */
	public Vector3(Vector3 a,Vector3 b){
		_x=a._x-b._x;
		_y=a._y-b._y;
		_z=a._z-b._z;
	}
	
	public double norm(){
		return Math.sqrt((double)(x()*x()+y()*y()+z()*z()));
	}

	public double distance(Vector3 v){
		 Vector3 v2 = new Vector3(this, v);
		 return v2.norm();
	}
	
	public Vector3 mult(double scalar){
		return new Vector3(scalar*dx(),scalar*dy(),scalar*dz());
	}
	
	public Vector3 mod(int m){
		return new Vector3(x()%m,y()%m,z()%m);
	}
	
	public Vector3 add(Vector3 v){
		return new Vector3(x()+v.x(),y()+v.y(),z()+v.z());
	}
	
	public double dx() {
		return (double)_x;
	}
	public int x() {
		return _x;
	}
	public void setX(int x) {
		_x = x;
	}
	public double dy() {
		return (double)_y;
	}
	public int y() {
		return _y;
	}
	public void setY(int y) {
		_y = y;
	}
	public double dz() {
		return (double)_z;
	}
	public int z() {
		return _z;
	}
	public void setZ(int z) {
		_z = z;
	}

}
