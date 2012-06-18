package textmode.curses.term;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.InterruptibleChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import textmode.curses.TerminalResizedReceiver;
import textmode.curses.net.SocketIO;
import textmode.curses.term.io.NoWaitIOLock;
import textmode.curses.term.termcap.TermType;



public class Terminal {
	
	
	private class StringWithPos{
		int pos = 0;
		String string;
		public StringWithPos(String s){ string = s; }
	}


	private Lock readLock  = new ReentrantLock(true);
	private Lock writeLock = new ReentrantLock(true);

	
	private int cols;
	private int lines;
	private NoWaitIOLock nwLock = null;
	private TermType type;
	private InputStream  in;
	private OutputStream out;
	private InterruptibleChannel channel = null;
	@SuppressWarnings("unused")
	private int PC = 0;
	private boolean ansiColors = false;
	private int numColors = 1;
	
	private char[] wrbuffer = new char[1024]; 
	private int    wroffset = 0;
	private ArrayList<TerminalResizedReceiver> receivers = new ArrayList<TerminalResizedReceiver>();
	
	public Terminal(TermType t, SocketIO sock) throws IOException{
		type = t;
		
		in = sock.getInputStream();
		out  = sock.getOutputStream();
		channel = sock.getIOLock().channel();
		
		nwLock = sock.getIOLock();
		
		getFlags();
	}
	
	public  void acquireRead() throws InterruptedException{
		readLock.lock();
	}
	
	public  void acquireWrite() throws InterruptedException{
		writeLock.lock();
	}
	
	public  void releaseRead() throws InterruptedException{
		readLock.unlock();
	}
	
	public  void releaseWrite() throws InterruptedException{
		writeLock.unlock();
	}
	
	
	public void wakeup(){
		if(nwLock != null)
			nwLock.wakeup();
	}
	
	protected final void setPC(String v){
		if(v!=null)
			PC = escapeSequence(new StringWithPos(v));
	}
	
	public final void setLines(int v){
		lines = v;
	}
	
	public final void setCols(int v){
		cols = v;
	}
	
	protected final void setNumColors(int v){
		numColors = v;
	}
	
	protected final TermType type(){
		return type;
	}
	
	public void resized(){
		for(TerminalResizedReceiver rcv: receivers)
			rcv.terminalResized(cols, lines);
	}
	
	public void addResizedReceiver(TerminalResizedReceiver rcv){
		receivers.add(rcv);
	}
	
	public Terminal(TermType t){
		type = t;
	    in   = new FileInputStream (FileDescriptor.in );
		out  = new FileOutputStream(FileDescriptor.out);
		channel = ((FileInputStream)in).getChannel();
		getFlags();
	}
	
	public void closeChannel() throws IOException{
		if(nwLock!=null)
			nwLock.stopSelecting();
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
	
	public void replaceOutputStream(OutputStream o){
		out = o;
	}
	
	public int getNumColors(){
		return numColors;
	}
	
	protected void getFlags(){
		String pc = type.getStr("pc");
		if(pc!=null)
			PC = escapeSequence(new StringWithPos(pc));
		
		ansiColors = type.getFlag("AX");
		numColors  = type.getNum("Co");
		if(numColors<0)
			numColors = 1;
		
		cols = type.getNum("co");
		lines = type.getNum("li");
	}
	
	private void goWrite() throws IOException {
		for(int i=0;i<wroffset;i++)
			out.write( wrbuffer[i] );
		
		out.flush();
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
					e.printStackTrace();
				}
			}
		}
		return (char)-1;
	}
	
	/* must be nano sec*/
	private void paddOut(long time) throws IOException
	{
		//Padding not used on TCP/IP, disabled because it could delay things
		/* TODO: take the stream type into account: only pad on serial
		 * speed under the value in the corresponding capacity. do not pad over
		 * IP or locally.
		 *
		long start = System.nanoTime();
		while( (System.nanoTime()-start)<time )
			out.write(PC);
		
		out.flush();*/
	}
	
	
	protected double extractPadding(int nblines,String cmd){
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
	
	public boolean hasCommand(String name){
		String cap = type.getStr(name);
		return (cap!=null);
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
	

	public int getLines() {
		return lines;
	}
	
	public int getCols() {
		return cols;
	}
	
	
	public boolean canAnsiColor(){
		return ansiColors;
	}
	
	public void writeChar(char c) throws IOException{
		out.write((int)c);
		out.flush();
	}
	public char getChar() throws IOException{
		
		char c = (char)in.read();
		return c;
	}
	
}
