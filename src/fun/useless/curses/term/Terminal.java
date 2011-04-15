package fun.useless.curses.term;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import fun.useless.curses.term.io.ChannelInputStream;
import fun.useless.curses.term.io.ChannelOutputStream;
import fun.useless.curses.term.termcap.TermType;


public class Terminal {
	
	
	private class StringWithPos{
		int pos = 0;
		String string;
		public StringWithPos(String s){ string = s; }
	}


	private TermType type;
	private InputStream  in;
	private OutputStream out;
	private InterruptibleChannel channel = null;
	private int PC = 0;
	private char[] wrbuffer = new char[1024]; 
	private int    wroffset = 0;
	
	public Terminal(TermType t, InterruptibleChannel c) throws InvalidClassException{
		type = t;
		
		if(c instanceof ReadableByteChannel)
			in = new ChannelInputStream((ReadableByteChannel)c);
		else
			throw new InvalidClassException("Channel must be a ReadableByteChannel");
		if(c instanceof WritableByteChannel)
			out  = new ChannelOutputStream((WritableByteChannel)c);
		else
			throw new InvalidClassException("Channel must be a WritableByteChannel");	
		
		channel = c;
		getFlags();
	}
	
	public Terminal(TermType t){
		type = t;
	    in   = new FileInputStream (FileDescriptor.in );
		out  = new FileOutputStream(FileDescriptor.out);
		channel = ((FileInputStream)in).getChannel();
		getFlags();
	}
	
	public void closeChannel() throws IOException{
		if(channel!=null)
			channel.close();
	}
	
	public OutputStream getOutputStream(){
		return out;
	}
	
	public InputStream getInputStream(){
		return in;
	}
	
	public void replaceInputStream(InputStream i){
		in = i;
	}
	
	private void getFlags()
	{
		String pc = type.getStr("pc");
		if(pc!=null)
			PC = escapeSequence(new StringWithPos(pc));
	}
	
	private void goWrite() throws IOException {
		for(int i=0;i<wroffset;i++)
			out.write( wrbuffer[i] );
		
		wroffset = 0;
	}
	
	private void writeNextChar(StringWithPos str) {
		char chr = str.string.charAt(str.pos);
		
		if(chr=='\\')
			chr = escapeSequence(str);
		else
			str.pos++;
		
		wrbuffer[wroffset]=chr;
		wroffset++;
	}
	
	protected void pushBytes(byte[] b){
		for(int i=0;i<b.length;i++)
			wrbuffer[wroffset++] = (char)b[i];
	}
	
	private char escapeSequence(StringWithPos str)
	{
		if(str.string.charAt(str.pos)=='\\'){
			str.pos++;
			if(str.string.charAt(str.pos)=='E'){
				str.pos++;
				return '\033';
			}else{
				char[] arr = new char[8];
				int offset = 0;
				while(str.string.charAt(str.pos) >= 0x30 && 
					  str.string.charAt(str.pos) <= 0x39){
					arr[offset] = str.string.charAt(str.pos);
					str.pos++;
					offset++;
				}
				
				
				try{
					return (char)Integer.parseInt(new String(arr,0,offset),8);
				}catch(NumberFormatException e){
					//TODO no dump
					e.printStackTrace();
				}
			}
		}
		return (char)-1;
	}
	
	/* must be nano sec*/
	private void paddOut(long time) throws IOException
	{
		/* TODO: take the stream type into account: only pad on serial
		 * speed under the value in the corr. capacity. don't pad over
		 * IP or local
		 */
		long start = System.nanoTime();
		while( (System.nanoTime()-start)<time )
			out.write(PC);
	}
	
	private double extractPadding(int nblines,String cmd){
		int i=0;
		StringBuilder b = new StringBuilder();
		while( (cmd.charAt(i) >= 0x30 && cmd.charAt(i) <= 0x39) || cmd.charAt(i)=='.')
		{
			b.append(cmd.charAt(i));
			i++;
		}
		double val = 0;
		
		if(b.length()>0)
			val =Double.parseDouble(b.toString());
		if(cmd.charAt(i)=='*')
			val *= nblines;
		
		return val;
	}
	public void writeString(String str)
	{
		StringWithPos ptr = new StringWithPos(str);
		
		while(ptr.pos<ptr.string.length())
			writeNextChar(ptr);
	}
	private int[] getBuffer(){
		int[] r = new int[wroffset];
		for(int n=0;n<wroffset;n++)
			r[n]=wrbuffer[n];
		
		wroffset = 0;
		return r;
	}
	
	public int[] getCapacity(String name){
		String cap = type.getStr(name);
		if(cap==null)
			return null;
		
		writeString(cap);
		
		return getBuffer();
	}
	
	public void writeCommand(String cmd, int nblines, int ... params) throws IOException{
		String cCmd       = type.getStr(cmd);
		if(cCmd!=null){
			/* TODO: Also use default paddings in term type */
			double p = extractPadding(nblines,cCmd);
			long paddingTime = (long)((double)(p*(double)1000))*(long)1000;

			Formatter fmt     = new Formatter(cCmd);
			String fCmd       = fmt.format(params);

			writeString(fCmd);
			
			goWrite();
			
			paddOut(paddingTime);
		}
	}

	public void closeStreams() throws IOException{
		out.close();
		in.close();
	}
	
	/* some programs can follow terminal emulator window size 
	 * still don't know how it works. I assume it requires IO 
	 * so I throw the not thrown IOException 
	 */
	public int getLines() throws IOException{
		return type.getNum("li");
	}
	
	public int getCols() throws IOException{
		return type.getNum("co");
	}
	
	public int getColorCount() throws IOException{
		return type.getNum("Co");
	}
	
	public void writeChar(char c) throws IOException{
		out.write((int)c);
	}
	public char getChar() throws IOException{
		
		char c = (char)in.read();
		return c;
	}
	
}
