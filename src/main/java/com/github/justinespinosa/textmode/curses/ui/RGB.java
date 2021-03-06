package com.github.justinespinosa.textmode.curses.ui;

import com.github.justinespinosa.textmode.curses.ui.util.Vector3;
import com.github.justinespinosa.textmode.xfer.util.Arrays;
import com.github.justinespinosa.textmode.xfer.util.Arrays.Endianness;

public class RGB extends Vector3{

	public final static RGB BLACK = new RGB(0,0,0);
	public final static RGB WHITE = new RGB(255,255,255);

	private int index;
	private static int chop(int c){
		if(c<0) return 0;
		if(c>254) return 254;
		return c;
	}
	
	public RGB(int r,int g,int b,int idx){
		super(chop(r),chop(g),chop(b));
		index=idx;
	}
	
	public RGB(int r,int g,int b){
		this(r,g,b,-1);
	}
	
	public RGB(Vector3 v){
		this(v.x(),v.y(),v.z());
	}
	
	public RGB(RGB a,RGB b){
		super(a,b);
		index = -1;
	}

	public RGB(int packedRGB){
		this(Arrays.fromInt(packedRGB, Endianness.Big));
	}
	
	public RGB(int[] rgbArray){
		this(rgbArray[0],rgbArray[1],rgbArray[2]);
	}
	
	protected RGB(byte[] rgbArray){
		this((int)rgbArray[0],(int)rgbArray[1],(int)rgbArray[2]);
	}
	
	public int index(){
		return index;
	}

	public int[] rgbBands(){
		int[] bands = new int[3];
		bands[0] = r();
		bands[1] = g();
		bands[2] = b();
		return bands;
	}
	
	@Override
	public String toString() {
		return "R:"+r()+";G:"+g()+";B:"+b();
	}
	
	public RGB dup() {
		return new RGB(r(),g(),b(),index);
	}
	public int r() {
		return x();
	}
	public int b() {
		return z();
	}
	public int g() {
		return y();
	}
}