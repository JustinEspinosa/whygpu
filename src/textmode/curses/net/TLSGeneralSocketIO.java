package textmode.curses.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.Status;

import textmode.curses.term.io.NoWaitIOLock;
import textmode.util.LegacyJavaHelper;


/**
 * Gives out Input/Output streams that encrypts/decrypts the In/Out of the socket
 * @author justin
 *
 */
public class TLSGeneralSocketIO  extends SocketIO{
	
	
	private class TLSOutputStream extends OutputStream{
		
		private Lock lock = new ReentrantLock();
		
		private void implFlush() throws IOException{
			outAppData.flip();
			encryptAndWrite(outAppData);
			outAppData.clear();
		}


		@Override
		public  void write(int b) throws IOException {
			
			try{
				lock.lock();
			
				outAppData.put((byte)b);

			}finally{
				lock.unlock();
			}
		}
		
		@Override
		public  void flush() throws IOException {
			try{
				lock.lock();

				implFlush();
				
			}finally{
				lock.unlock();
			}
		}
	}
	
	private class TLSInputStream extends InputStream{
		@Override
		public int read() throws IOException {
			try{
				synchronized(inAppData){
					if (!inAppData.hasRemaining()){

						if (sslEngine.isInboundDone())
							return -1;
					
						readAndDecrypt();
					}
					return ((int)inAppData.get()&0xff);
				}
			}catch(SSLException e){
				logger.log(Level.SEVERE,"TLS Error",e);
			}
			return -1;
		}
	}
	
	private TLSInputStream  tlsIS;
	private TLSOutputStream tlsOS;
	private NoWaitIOLock    nwLock;
		
	private SSLEngine   sslEngine;
	private ByteBuffer  inAppData;
	private ByteBuffer  inNetData;
	private ByteBuffer  outAppData;
	private ByteBuffer  outNetData;
	
	private boolean logging = true;
	private Logger logger;
	
	private WritableByteChannel channelOut;
	private ReadableByteChannel channelIn;
	
	public TLSGeneralSocketIO(SocketChannel sock, SSLContext ctx) throws IOException{
		this(null,sock,ctx);	
	}
	
	public TLSGeneralSocketIO(Logger l,SocketChannel sock, SSLContext ctx) throws IOException{
		super(sock);
		
		logger = l;
		
		tlsIS = new TLSInputStream();
		tlsOS = new TLSOutputStream();
		
		nwLock = new NoWaitIOLock(sock);
		
		if(nwLock.channel() instanceof WritableByteChannel)
			channelOut = (WritableByteChannel)nwLock.channel();
		else
			logger.fine("Is not writablebytechannel");

		if(nwLock.channel() instanceof ReadableByteChannel)
			channelIn = (ReadableByteChannel)nwLock.channel();
		else
			logger.fine("Is not readablebytechannel");
		
		sslEngine  = ctx.createSSLEngine();
		sslEngine.setUseClientMode(false);
		//sslEngine.setEnabledCipherSuites(cipherSuites);
		
		SSLSession session = sslEngine.getSession();
		
		inAppData = ByteBuffer.allocateDirect(session.getApplicationBufferSize());
		inNetData = ByteBuffer.allocateDirect(session.getPacketBufferSize());
		outAppData = ByteBuffer.allocateDirect(session.getApplicationBufferSize());
		outNetData = ByteBuffer.allocateDirect(session.getPacketBufferSize());
		
		//fake these buffers to 'read until end.'
		inAppData.position(inAppData.limit());
		outNetData.position(inNetData.limit());
		
		outAppData.clear();
		inNetData.clear();
		try{
			handshake();
		}catch(SSLException e){
			nwLock.stopSelecting();
			throw e;
		}
		

	}
	
	@Override
	public NoWaitIOLock getIOLock() {
		return nwLock;
	}
		
	private boolean canLog(){
		if(logger==null) return false;
		return logging;
	}
	
	private void handshake() throws IOException {
				
		sslEngine.beginHandshake();
		
		if(canLog())
			logger.fine("TLS Handshake");
		
		doHandshake();
				
		if(canLog())
			logger.fine("TLS Handshake finished");
	}

	
	private void doHandshake() throws IOException {

		SSLEngineResult.HandshakeStatus hsStatus = sslEngine.getHandshakeStatus();
		ByteBuffer dummy = ByteBuffer.allocate(0);
		
		while (hsStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING ) {

			if(canLog())
				logger.fine("TLS Handshake status:"+hsStatus);
			
			switch (hsStatus) {
			
			case FINISHED:
				return;
				
			case NEED_TASK:
				Runnable task;
				while ((task = sslEngine.getDelegatedTask()) != null) 
					task.run();
				break;
				
			case NEED_UNWRAP:
				readAndDecrypt();
				break;

			case NEED_WRAP:
				encryptAndWrite(dummy);
				break;
			}
			
			hsStatus = sslEngine.getHandshakeStatus();
		}

	}

	
	public InputStream getInputStream() throws IOException{
		return tlsIS;
	}
	
	public OutputStream getOutputStream() throws IOException{
		return tlsOS;
	}
	
	
	private int encryptAndWrite(ByteBuffer out) throws IOException{
		return doEncryptAndWrite(out);
	}

	private int doEncryptAndWrite(ByteBuffer out) throws IOException{
		if(canLog())
			logBufferState(out,"outAppData");
		
		outNetData.clear();
			
		SSLEngineResult status = sslEngine.wrap(out, outNetData);
	
		if(status.getStatus() == Status.CLOSED)
			throw new SSLException("SSL Closed");
			
		if(canLog())
			logger.fine("Status after wrap: "+status);
			
		outNetData.flip();
			
		int written = 0;
			
		try{
			while(outNetData.hasRemaining()){
				
				boolean done = false;
				while(!done){
					try{
						nwLock.wantWrite();
						done = true;
					}catch(InterruptedException e){
					}
				}
					
				written += channelOut.write(outNetData);
				
				if(canLog())
					logger.fine("Wrote "+written+", TCP Buffer is: "+ getSocket().socket().getSendBufferSize());
			
				
			}
			
		}finally{
			nwLock.doneWrite();
		}
		
		
		return written;
		
	}
	
	private void logBufferState(ByteBuffer b, String name){
		ByteBuffer buff = b.duplicate();
		StringBuilder builder = new StringBuilder();
		builder.append("State of ");
		builder.append(name);
		builder.append('\n');
		builder.append("Position: ");
		builder.append(buff.position());
		builder.append(", Limit: ");
		builder.append(buff.limit());
		builder.append(", Capacity: ");
		builder.append(buff.capacity());
		builder.append('\n');
		builder.append("First bytes:");
		builder.append('\n');
			
		for(int j=0;j<1 && buff.hasRemaining();j++){
			for(int i=0;i<20 && buff.hasRemaining();i++)
				builder.append(String.format("%02X", buff.get()));
			builder.append('\n');
		}
		
		logger.fine(builder.toString());
		
	}
	
	private SSLEngineResult doReadAndDecrypt() throws IOException{
		int countRead = 0;
		
		
		if(inNetData.position()==0){
			
			if(canLog())
				logger.fine("TLS tries to read from socket");
			
			try{
				
				nwLock.wantRead();
					 
				countRead = channelIn.read(inNetData);
				
			}catch(InterruptedException e){
				throw LegacyJavaHelper.throwWithCause(new IOException(),e);
			}finally{
				nwLock.doneRead();
			}
			
			
			if(canLog())
				logger.fine("TLS read "+countRead+" bytes to decrpyt");
			
			if(countRead<0){
				//stream is finished
				sslEngine.closeInbound();
				return null;
			}
		}
		
		
		//start at begin of buffer
		inNetData.flip();		
		
		SSLEngineResult status = sslEngine.unwrap(inNetData, inAppData);
		
		inNetData.compact();
		
		return status;
	}
	
	private boolean continueReading(SSLEngineResult result) throws IOException{
		doHandshake();
		return ( result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && 
		          result.bytesProduced() == 0);
	}
	
	
	private void readAndDecrypt() throws IOException{
		SSLEngineResult result;
	
		inAppData.clear();
		try{
			do{
				try{
					result = doReadAndDecrypt();
				}catch(SSLException e){
					logger.log(Level.SEVERE, "SSL error while reading:", e);
					throw e;
				}

				if(result.getStatus() == SSLEngineResult.Status.CLOSED)
					throw new IOException("SSL Closed.");
				
			// Sometimes, TLS Protocol stuff are done and no data is produced: repeat
			}while ( continueReading(result) );
		
		}finally{
			inAppData.flip();
		
			if(canLog())
				logBufferState(inAppData,"inAppData");
		}
		
	}

	
}
