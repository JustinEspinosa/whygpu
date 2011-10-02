package textmode.xfer.zm.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import textmode.xfer.io.ObjectInputStream;
import textmode.xfer.util.ByteBuffer;
import textmode.xfer.util.CRC;
import textmode.xfer.util.CRC.Type;
import textmode.xfer.zm.packet.Cancel;
import textmode.xfer.zm.packet.DataPacket;
import textmode.xfer.zm.packet.Finish;
import textmode.xfer.zm.packet.Format;
import textmode.xfer.zm.packet.Header;
import textmode.xfer.zm.proto.Action;
import textmode.xfer.zm.proto.Escape;
import textmode.xfer.zm.util.ZMPacket;
import textmode.xfer.zm.util.ZModemCharacter;


public class ZMPacketInputStream extends ObjectInputStream<ZMPacket> {
	
	private static byte[] _ignored = {0x11,0x13,(byte) 0x91,(byte) 0x93};
	private InputStream netIs;
	private CRC dataCRC = new CRC(Type.CRC16);
	private boolean gotFIN = false;
	private boolean acceptsHeader = true;
	
	public ZMPacketInputStream(InputStream is){
		netIs = is;
	}
	
	private boolean ignored(byte b){
		boolean r = (Arrays.binarySearch(_ignored, b) >= 0);
		return r;
	}
	
	private byte implRead() throws IOException{
		int n;
		while(ignored((byte)(n = netIs.read())));
		return (byte)n;
	}
	
	@Override
	public ZMPacket read() throws IOException{
		ByteBuffer zbuff = ByteBuffer.allocateDirect(4096);
		boolean doread = true;
		Action action = Action.ESCAPE;
		
		int beforeStop = -1;
		int countCan = 0;
		
		while(doread){
			byte n = implRead();
			if(gotFIN && n=='O'){
				n = implRead();
				if(n=='O'){
					return new Finish();
				}
			}

			if(n == ZModemCharacter.ZDLE.value()){
				n = (byte)netIs.read();
				
				if(n == ZModemCharacter.ZDLE.value())
					countCan+=2;
				else
					countCan=0;
				
				Escape escape = Escape.detect(n,acceptsHeader);
							
				if(escape.action() != Action.ESCAPE && beforeStop<0){
					action = escape.action();
					
					if(escape.action() == Action.DATA)
						beforeStop = dataCRC.size();
					else
						beforeStop = escape.len();
					
					dataCRC.update(n);
				}else{
					n = Escape.escapeIt(n);
				}
									
			}
			zbuff.put(n);
			
			if(beforeStop<0)
				dataCRC.update(n);
						
			if(beforeStop==0)
				doread = false;
			
			if(beforeStop>0)
				beforeStop--;
			
			if(countCan>=5){
				doread = false;
				action = Action.CANCEL;
			}
				
		}
		
		zbuff.flip();
		
		ZMPacket r = null;
		switch(action){
		case HEADER:
			r = Header.unmarshall(zbuff);
			
			
			if(((Header)r).format()==Format.BIN32)
				dataCRC = new CRC(Type.CRC32);
			else
				dataCRC = new CRC(Type.CRC16);
			
			if(((Header)r).type()==ZModemCharacter.ZFIN)
				gotFIN=true;
			if(((Header)r).type()==ZModemCharacter.ZDATA || ((Header)r).type()==ZModemCharacter.ZFILE)
				acceptsHeader = false;
			
			break;
		case DATA:
			dataCRC.finalize();

			r = DataPacket.unmarshall(zbuff,dataCRC);
			
			dataCRC = new CRC(dataCRC.type());
			
			if(((DataPacket)r).type()==ZModemCharacter.ZCRCG)
				acceptsHeader = false;
			else
				acceptsHeader = true;
			
			break;
		case CANCEL:
			r = new Cancel();
			dataCRC = new CRC(dataCRC.type());
			break;
		}
		
		//System.out.println(" << "+r);
		
		return r;
	}

}
