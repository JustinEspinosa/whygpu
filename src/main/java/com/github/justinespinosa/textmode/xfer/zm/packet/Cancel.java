package com.github.justinespinosa.textmode.xfer.zm.packet;

import com.github.justinespinosa.textmode.xfer.util.ASCII;
import com.github.justinespinosa.textmode.xfer.util.Buffer;
import com.github.justinespinosa.textmode.xfer.util.ByteBuffer;
import com.github.justinespinosa.textmode.xfer.zm.util.ZMPacket;

public class Cancel extends ZMPacket {

	@Override
	public Buffer marshall() {
		ByteBuffer buff = ByteBuffer.allocateDirect(16);
		
		for(int i=0;i<8;i++)
			buff.put(ASCII.CAN.value());
		for(int i=0;i<8;i++)
			buff.put(ASCII.BS.value());

		buff.flip();

		return buff;
	}

	@Override
	public String toString() {
		return "Cancel: CAN * 8 + BS * 8";
	}
}
