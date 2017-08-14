package com.github.justinespinosa.textmode.curses.term;

import java.util.Vector;

public class ByteVector extends Vector<Byte>{

	private static final long serialVersionUID = -6369884260453331887L;
	public String toString(){
		StringBuilder bldr = new StringBuilder();
		for(Byte b:this){
			bldr.append((char)b.byteValue());
		}
		return bldr.toString();
	}
	public byte[] toArray(int offset, int length){
		byte[] ret = new byte[length];
		for(int i=0;i<length;++i)
			ret[i] = get(offset+i).byteValue();
		return ret;
	}

}
