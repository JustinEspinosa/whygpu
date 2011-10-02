package shittytests;

import textmode.xfer.util.Arrays;
import textmode.xfer.util.Arrays.Endianness;

public class Draft {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		byte[] b = Arrays.fromInt(0x10000, Endianness.Big);
		
		for(byte a:b)
		System.out.printf("%d ",a);
	    
	    System.out.println("   ___ok");
				
		System.out.printf("%d \n",Arrays.toInt(b, Endianness.Big));

	}

}
