package shittytests;

import fun.useless.xfer.util.CRC;
import fun.useless.xfer.util.CRC.Type;

public class TestCrc {
	
	public static void main(String[] args){
		CRC crc = new CRC(Type.CRC16);
		crc.update((byte)0xff);
		crc.update((byte)0x6a);
		crc.finalize();
		
	}
	
}
