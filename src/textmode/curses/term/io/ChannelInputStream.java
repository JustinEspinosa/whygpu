package textmode.curses.term.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;


/**
 * Blocking read on any channel
 * @author justin
 *
 */
public class ChannelInputStream extends InputStream {
	
	private NoWaitIOLock nwLock;
	private ReadableByteChannel channel;
	
	public ChannelInputStream(NoWaitIOLock lock) throws InvalidClassException {
		super();
		nwLock = lock;
		if(nwLock.channel() instanceof ReadableByteChannel)
			channel = (ReadableByteChannel)nwLock.channel();
		else
			throw new InvalidClassException("Not a ReadableByteChannel");
	}

	@Override
	public int read() throws IOException {
		ByteBuffer b = ByteBuffer.allocate(1);
		
		if(channel.read(b)<1){
			nwLock.readWait();
			channel.read(b);
		}
				
		b.flip();
		
		return b.get();
			
	}

}
