package textmode.curses.term.io;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ChannelOutputStream extends OutputStream {

	private NoWaitIOLock nwLock;
	private WritableByteChannel channel;
	private ByteBuffer buff;

	public ChannelOutputStream(NoWaitIOLock lock) throws InvalidClassException {
		super();
		nwLock = lock;
		if(nwLock.channel() instanceof WritableByteChannel)
			channel = (WritableByteChannel)nwLock.channel();
		else
			throw new InvalidClassException("Not a WritableByteChannel");
		
		buff = ByteBuffer.allocate(1024);
	}
	
	private void writeBuff() throws IOException {
		buff.flip();
		
		//int toWrite = buff.remaining();
		//int written = 0;
		
		while( buff.hasRemaining() ){
			channel.write(buff);
			if( buff.hasRemaining() )
				nwLock.writeWait();
		}
			
		buff.clear();
	}
	
	@Override
	public void write(int b) throws IOException {
		
		buff.put((byte)b);
		
		if(!buff.hasRemaining()){
			writeBuff();
		}
		
	}

	@Override
	public void flush() throws IOException {
		writeBuff();
	}
}
