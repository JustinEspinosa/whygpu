package fun.useless.curses.term.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.ByteBuffer;

/**
 * Blocking read on any channel
 * @author justin
 *
 */
public class ChannelInputStream extends InputStream {
	
	private ReadableByteChannel channel;

	public ChannelInputStream(ReadableByteChannel ch) {
		super();
		channel = ch;
	}

	@Override
	public int read() throws IOException {
		ByteBuffer b = ByteBuffer.allocate(1);
		
		if(channel instanceof SelectableChannel){
			SelectableChannel sChan = (SelectableChannel)channel;
			if(sChan.isBlocking()){
				channel.read(b);
			}else{
				int num=0;
				while( (num=channel.read(b))<1){
					if(num < 0) return num;
					try{ Thread.sleep(50); }catch(InterruptedException ie){}
				}
			}
		}else{
			channel.read(b);
		}
		
		if(b.hasRemaining()) throw new IOException("No data read.");
		
		b.flip();
		
		return b.get();
			
	}

}
