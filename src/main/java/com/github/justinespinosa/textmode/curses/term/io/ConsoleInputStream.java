package com.github.justinespinosa.textmode.curses.term.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConsoleInputStream extends InputStream{
	
	private boolean canonMode = true;
	private boolean echoMode  = true;
	//private boolean utf8 = false;
	
	private InputStream baseIS;
	private OutputStream echoOut;
	private int   eolState = 0;
	private int   canonicalBufferLen = 0;
	private int   canonicalBufferOffset = 0;
	private int[] canonicalBuffer = new int[65536]; //2^16 should be sufficient for one line of text
	
	public ConsoleInputStream(InputStream is,OutputStream echo){
		baseIS = is;
		echoOut = echo;
	}
	/*
	public void setUtf8(boolean c){
		utf8 = c;
	}
	*/
	
	/**
	 * Switch between canonical (line by line) and non-canonical mode
	 * @param c
	 */
	public void setCanonical(boolean c){
		canonMode = c;
	}
	/**
	 * Set echo
	 * @param c
	 */
	public void setEcho(boolean e){
		echoMode = e;
	}
	
	private int readForMode() throws IOException{
		int c;
		
		if(canonMode){
			c = canonicalRead();
		}else{
			c = baseIS.read();
			if(echoMode)
				echoOut.write(c);
		}
		
		return c;
	}
	/*
	private int utf8getNumBytes(int b){
		int r = 0;
		int s = 0;
		
		while(b > 0){
			r = b % 2;
			if(r==0) 
				s = 0;
			else
				s += r;
			b = b /2;
		}
		
		return s;
	}
	*/
	
	/**
	 * Read one byte using input mode
	 */
	@Override
	public int read() throws IOException {
		return readForMode();
	}
	
	private boolean isEol(int c){
		return ( c == 10 || c == 13  );
	}
	
	//currently minimalistic: don't work with escape sequences
	private void editStream() throws IOException{
		boolean echoed = false;
		int chr = canonicalBuffer[canonicalBufferOffset];
		
		//ignore up/down arrow
		if(chr!= 38 && chr!=40){
			if(chr == 8){
				if(canonicalBufferOffset>0){
					canonicalBufferOffset--;

					if(echoMode){
						echoOut.write(8);
						echoOut.write(' ');
						echoOut.write(8);
					}
				}
				
				echoed=true;
			}
			//left arrow
			if(chr == 37){
				if(canonicalBufferOffset>0){
					canonicalBufferOffset--;

					if(echoMode)
						echoOut.write(chr);
				}
				echoed=true;
			}
			//left arrow
			if(chr == 39){
				if(canonicalBufferOffset<canonicalBufferLen){
					canonicalBufferOffset++;

					if(echoMode)
						echoOut.write(chr);
				}
				echoed=true;
			}
			if(!echoed && echoMode)
				echoOut.write(chr);
		}
	}
	private void bufferUntilEol() throws IOException{
		canonicalBuffer[canonicalBufferOffset] = baseIS.read();
		editStream();
		while(!isEol(canonicalBuffer[canonicalBufferOffset])){
			canonicalBufferOffset++;
			if(canonicalBufferOffset>=canonicalBufferLen)
				canonicalBufferLen=canonicalBufferOffset+1;
			
			canonicalBuffer[canonicalBufferOffset] = baseIS.read();
			editStream();
		}
	}
	
	private int readNext() throws IOException{
		return canonicalBuffer[canonicalBufferOffset++];
	}
	
	private void buffer() throws IOException{
		bufferUntilEol();
		
		//skip 2 consecutive different eol char
		if(canonicalBufferOffset==0)
			if(eolState>0 && eolState!=canonicalBuffer[canonicalBufferOffset])
				bufferUntilEol();
		
		eolState=canonicalBuffer[canonicalBufferOffset];
		
		canonicalBufferOffset  = 0;
		
	}
	
	private int canonicalRead() throws IOException{
		if(canonicalBufferOffset >= canonicalBufferLen)
			buffer();
		
		return readNext();
	}
}
