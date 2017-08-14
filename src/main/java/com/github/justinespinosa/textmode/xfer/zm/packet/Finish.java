package com.github.justinespinosa.textmode.xfer.zm.packet;

import com.github.justinespinosa.textmode.xfer.util.Buffer;
import com.github.justinespinosa.textmode.xfer.util.ByteBuffer;
import com.github.justinespinosa.textmode.xfer.zm.util.ZMPacket;

public class Finish extends ZMPacket {

	@Override
	public Buffer marshall() {
		ByteBuffer buff = ByteBuffer.allocateDirect(16);
		
		for(int i=0;i<2;i++)
			buff.put((byte) 'O');
		
		buff.flip();
		
		return buff;
	}
	
	@Override
	public String toString() {
		return "Finish: OO";
	}

}
