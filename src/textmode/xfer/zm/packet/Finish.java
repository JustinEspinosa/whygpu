package textmode.xfer.zm.packet;

import textmode.xfer.util.Buffer;
import textmode.xfer.util.ByteBuffer;
import textmode.xfer.zm.util.ZMPacket;

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
