package fun.useless.curses.term.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelOutputStream extends OutputStream {

	private WritableByteChannel channel;

	public ChannelOutputStream(WritableByteChannel ch) {
		super();
		channel = ch;
	}
	@Override
	public void write(int b) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put((byte)b);
		bb.flip();
		
		if(channel instanceof SelectableChannel){
			SelectableChannel sChan = (SelectableChannel)channel;
			if(sChan.isBlocking()){
				channel.write(bb);
			}else{
				while( channel.write(bb)<1 ){
					try{ Thread.sleep(50); }catch(InterruptedException ie){}
				}
			}
		}else{
			channel.write(bb);
		}
		
		if(bb.hasRemaining()) throw new IOException("No data written.");
	}

}
