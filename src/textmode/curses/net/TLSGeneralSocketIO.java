package textmode.curses.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import textmode.curses.term.io.NoWaitIOLock;


/**
 * Gives out Input/Output streams that encrypts/decrypts the In/Out of the socket
 * @author justin
 *
 */
public class TLSGeneralSocketIO  extends SocketIO{
	
	
	private class TLSOutputStream extends OutputStream{
		
		private class Flusher extends Thread{
			public void run() {
				
				try{
					sleep(70);
				}catch(InterruptedException e){					
				}
				
				
				try{	
					implFlush2();
				}catch (IOException e) {
					logger.log(Level.SEVERE,"could not flush",e);
				}catch (Exception e) {
					logger.log(Level.SEVERE,"Uncaught on flush",e);
				}

			};
		}

		private static final int MinFree = 512;
		
		private Thread delay = null;

		private void implFlush2() throws IOException{
			synchronized(outAppData){
				outAppData.flip();
				encryptAndWrite(outAppData);
				outAppData.clear();
			}
		}
		
		private void implFlush() {
			
			if(delay==null){
				delay = new Flusher();
				delay.start();
			}else{
				synchronized(delay){
					if(!delay.isAlive()){
						delay = new Flusher();
						delay.start();
					}
				}
			}
			
			if(outAppData.position()>=outAppData.capacity()-MinFree){
				synchronized(delay){
					delay.interrupt();
					try {
						delay.join();
					} catch (InterruptedException e) {}
				}
				delay=null;
			}
			
		}
		@Override
		public synchronized void write(int b) throws IOException {
			boolean forceFlush = false;
			
			synchronized(outAppData){
				outAppData.put((byte)b);
				forceFlush = (outAppData.position()>=outAppData.capacity()-MinFree);
			}
			
			if(forceFlush) implFlush();
		}
		
		@Override
		public synchronized void flush() throws IOException {
			implFlush();
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
	
	public TLSGeneralSocketIO(SocketChannel sock, SSLContext ctx) throws IOException{
		this(null,sock,ctx);	
	}
	
	public TLSGeneralSocketIO(Logger l,SocketChannel sock, SSLContext ctx) throws IOException{
		super(sock);
		
		logger = l;
		
		tlsIS = new TLSInputStream();
		tlsOS = new TLSOutputStream();
		
		nwLock = new NoWaitIOLock(sock);
		nwLock.start();
		
		sslEngine  = ctx.createSSLEngine();
		sslEngine.setUseClientMode(false);
		
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
		
		outNetData.clear();
			
		SSLEngineResult status = sslEngine.wrap(out, outNetData);
	
			
		if(canLog())
			logger.fine("Status after write: "+status);
			
		outNetData.flip();
			
		int written = outNetData.remaining();
			
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
				
				getSocket().write(outNetData);
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
					 
				countRead = getSocket().read(inNetData);
				
			}catch(InterruptedException e){
				throw new IOException(e);
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

				// Sometimes, TLS Protocol stuff are done and no data is produced: repeat
				if(result.getStatus() == SSLEngineResult.Status.CLOSED)
					throw new IOException("SSL Closed.");
			
			}while ( continueReading(result) );
		
		}finally{
			inAppData.flip();
		
			if(canLog())
				logBufferState(inAppData,"decrypted incomming data");
		}
		
	}

	
}
