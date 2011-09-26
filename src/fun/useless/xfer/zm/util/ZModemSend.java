package fun.useless.xfer.zm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import fun.useless.xfer.util.Arrays;
import fun.useless.xfer.util.InvalidChecksumException;
import fun.useless.xfer.zm.io.ZMPacketInputStream;
import fun.useless.xfer.zm.io.ZMPacketOutputStream;
import fun.useless.xfer.zm.packet.Cancel;
import fun.useless.xfer.zm.packet.DataPacket;
import fun.useless.xfer.zm.packet.Finish;
import fun.useless.xfer.zm.packet.Format;
import fun.useless.xfer.zm.packet.Header;

public class ZModemSend {
	
	private static final int packLen = 1024;
	
	private Map<String,File> files;
	private Iterator<String> iter;
	private File file;
	private String fileName;
	private int fOffset = 0;
	private boolean atEof = false;
	private FileInputStream fileIs;
	private InputStream netIs;
	private OutputStream netOs;
	
	
	public ZModemSend(Map<String,File> fls,InputStream netin,OutputStream netout) throws IOException{
		files = fls;
		iter  = files.keySet().iterator();

		fOffset = 0;
		netIs  = netin;
		netOs  = netout;
	}
	
	public boolean nextFile() throws IOException{
		
		if(!iter.hasNext())
			return false;
		
		fileName = iter.next();
		
		file = files.get(fileName);
		fileIs = new FileInputStream(file);
		fOffset = 0;
		
		return true;
	}
	
	private void position(int offset) throws IOException{
		if(offset!=fOffset){
			fileIs.close();
			fileIs = new FileInputStream(file);
			fileIs.skip(offset);
			fOffset = offset;
		}
	}
	
	private byte[] getNextBlock() throws IOException{
		byte[] data = new byte[packLen];
		int len;
		
		len = fileIs.read(data);
		
		/* we know it is a file: all the data is locally available.*/
		if(len<data.length)
			atEof = true;

		fOffset += len;
		
		if(len!=data.length)
			return Arrays.copyOf(data,len);
		else
			return data;
	}
	
	private DataPacket getNextDataPacket() throws IOException{
		byte[] data = getNextBlock();
		ZModemCharacter fe = ZModemCharacter.ZCRCW;
		if(atEof){
			fe = ZModemCharacter.ZCRCE;
			fileIs.close();
		}
		
		return new DataPacket(fe, data);
	}
	
	public void send() {
		ZMPacketFactory factory = new ZMPacketFactory();
		
		ZMPacketInputStream is = new ZMPacketInputStream(netIs);
		ZMPacketOutputStream os = new ZMPacketOutputStream(netOs);
		
		
		try{
			
			boolean end = false;
			int errorCount = 0;
			ZMPacket packet = null;
			
			while(!end){
				
				try{
					packet = is.read();
				}catch(InvalidChecksumException ice){
					++errorCount;
					if(errorCount>20){
						os.write(new Cancel());
						end = true;
					}
				}
				
				if(packet instanceof Cancel){
					end = true;
				}
				
				if(packet instanceof Header){
					Header header = (Header)packet;
					
					switch(header.type()){
					case ZRINIT:
						if(!nextFile()){
							os.write(new Header(Format.BIN, ZModemCharacter.ZFIN));
						}else{
							os.write( new Header(Format.BIN, ZModemCharacter.ZFILE, new byte[]{0,0,0,ZMOptions.with(ZMOptions.ZCBIN)}) );
							os.write( factory.createZFilePacket(fileName, file.length()));
						}
						break;
					case ZRPOS:
						if(!atEof)
							position(header.getPos());
					case ZACK:	
						os.write(new Header(Format.BIN, ZModemCharacter.ZDATA, fOffset));
						os.write(getNextDataPacket());
						if(atEof)
							os.write(new Header(Format.HEX, ZModemCharacter.ZEOF, fOffset));
						break;
					case ZFIN:
						end = true;
						os.write(new Finish());
						break;
					default:
						end = true;
						os.write(new Cancel());
						break;
					}
					
				}
			}
		
		
		}catch (IOException e) {
			System.out.println("IO Exception in file: "+file+", "+e.getMessage());
		}
		
	}
}
