package com.github.justinespinosa.shittytests;

import com.github.justinespinosa.textmode.xfer.util.CRC;
import com.github.justinespinosa.textmode.xfer.util.CRC.Type;
import org.junit.Assert;
import org.junit.Test;

public class TestCrc {
	
	@Test
	public void testCRC(){

		CRC crc = new CRC(Type.CRC16);
		crc.update((byte)0xff);
		crc.update((byte)0x6a);
		crc.finalize();

		Assert.assertArrayEquals(new byte[]{(byte)0xce,0x13},crc.getBytes());
		
	}
	
}
