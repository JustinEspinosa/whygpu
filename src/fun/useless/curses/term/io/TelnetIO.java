package fun.useless.curses.term.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import fun.useless.curses.term.ByteVector;



public class TelnetIO {

	/* command set */
	protected final static byte IAC  = (byte)255;
	protected final static byte DONT = (byte)254;
	protected final static byte DO   = (byte)253;
	protected final static byte WONT = (byte)252;
	protected final static byte WILL = (byte)251;
	protected final static byte SB   = (byte)250;
	protected final static byte GA   = (byte)249;
	protected final static byte EL   = (byte)248;	
	protected final static byte EC   = (byte)247;	
	protected final static byte AYT  = (byte)246;	
	protected final static byte AO   = (byte)245;	
	protected final static byte IP   = (byte)244;	
	protected final static byte BRK  = (byte)243;	
	protected final static byte DM   = (byte)242;	
	protected final static byte NOP  = (byte)241;	
	protected final static byte SE   = (byte)240;
	protected final static byte NULL = (byte)0;
	
	/* some useful options */
	public final static byte TERMINAL_TYPE = (byte)24;
	public final static byte LINEMODE = (byte)34;
	public final static byte ECHO = (byte)1;
	public final static byte SUPPRESS_GOAHEAD = (byte)3;
	
	protected static void writeSequence(OutputStream os, byte ... c) throws IOException{
		os.write(c);
	}

	protected static void writeOption(OutputStream os,byte o,byte a) throws IOException{
		writeSequence(os,IAC,a,o);
	}
	protected static void writeSubOption(OutputStream os, byte o,ByteVector str) throws IOException{
		
		byte[] r = new byte[str.size()+5]; int i=0;
		
		r[i++]=IAC; r[i++]=SB; r[i++]=o;
		for(int n=0;n<str.size();n++) r[i++]=str.elementAt(n); 
		r[i++]=IAC; r[i++]=SE;
		
		writeSequence(os,r);
	}
	protected static boolean multiOr(byte c1,byte ... c2){
		for(int n=0;n<c2.length;n++) if(c1==c2[n]) return true;
		return false;
	}
	
	protected OutputStream os;
	protected InputStream is;
	
	protected byte[]    lastActionSent  = new byte[256];
	protected byte[]    lastActionRecv  = new byte[256];
	protected boolean[] inSubNeg        = new boolean[256];
	/* cannot create generic arrays? why? uncool. I'm upsetings*/
	@SuppressWarnings("rawtypes")
	protected Vector[]  subNegHistory   = new Vector[256];
	
	
	/* but I want generic arrayings  !! */
	@SuppressWarnings("rawtypes")
	public TelnetIO(InputStream i,OutputStream o){
		is=i;
		os=o;
		for(int n=0;n<256;n++){ 
			lastActionSent[n]=0;
			inSubNeg[n]=false; 
			subNegHistory[n] = new Vector();
		}
	}
	

	public InputStream makeHookedInputStream(){
		return new InputStream(){
			@Override
			public int read() throws IOException {
				return telnetRead();
			}
		};
	}
	
	public int telnetRead() throws IOException{
		return telnetRead(false);
	}
	public int telnetRead(boolean returnsiac) throws IOException{
		int b = is.read();
		
		if( (byte)b == IAC ){
			readCommand();
			if(!returnsiac)
				b = is.read();
		}
		return b;
	}
	
	protected final boolean actionToBool(byte action){
		switch(action){
		case WILL:
		case DO:
			return true;
		}
		return false;
	}
	
	public boolean getOptionStatus(byte option){		
		if( lastActionRecv[option] == NULL)
			return actionToBool(lastActionSent[option]);
		else
			return actionToBool(lastActionRecv[option]);
	}
	

	public ByteVector getLastSubNegotiation(byte option){
		return (ByteVector)subNegHistory[option].lastElement();
	}
	
	
	protected final byte positiveOppositeAction(byte action){
		switch(action){
		case WILL: return DO;
		case WONT: return DONT;
		case DO: return WILL;
		case DONT: return WONT;
		}
		return NULL;
	}
	
	protected final byte negativeOppositeAction(byte action){
		switch(action){
		case WILL: return DONT;
		case WONT: return DO;
		case DO: return WONT;
		case DONT: return WILL;
		}
		return NULL;
	}
	
		
	protected void action(byte action,byte option) throws IOException{
		
		//System.out.println("OP : "+(int)action+ " : "+ option);
		
		lastActionRecv[option] = action;
		
		//this implementation refuses any unrequested client option. server has full control.
		if(lastActionSent[option] == 0)
			singleOption(negativeOppositeAction(action), option);
		
		if(action == positiveOppositeAction(lastActionSent[option]))
			inSubNeg[option] = true;

	}
	
	@SuppressWarnings("unchecked")
	protected void negotation(byte option) throws IOException{
		
		//System.out.println("NEG : "+ option);
		
		if(inSubNeg[option]){
			subNegHistory[option].add( readNegotiationString() );
		}
	}
	
	protected void readCommand() throws IOException{
		byte c = (byte)is.read();
		
		//System.out.println("CMD: "+(int)c);
		
		/* warning: reversed because bytes are signed in java */
		if(c < IAC && c > SB)
			action(c,(byte)is.read());
		if(c == SB)
			negotation((byte)is.read());
		if(c == IP){
			//Close ungracefully, user must catch the exceptions
			is.close();
			os.close();
		}
	}
	
	protected ByteVector readNegotiationString() throws IOException{
		ByteVector bld = new ByteVector();
		byte b = (byte)is.read();
		while(b!=SE){
			if(b!=IAC)
				bld.add(b);
			b = (byte)is.read();
		}
		
		//System.out.println("SUB-STR: "+bld.toString());
		
		return bld;
	}
	
	protected void optionAction(byte action,byte option) throws IOException{
		writeOption(os, option, action);
		lastActionSent[option] = action;
	}
	
	/* I want genericsings */
	@SuppressWarnings("unchecked")
	protected void optionNegotiation(byte option,ByteVector negotiationString) throws IOException{
		writeSubOption(os,option,negotiationString);
		subNegHistory[option].add(negotiationString);
	}
	
	//These do not wait for the response
	protected void singleOptionNW(byte action,byte option) throws IOException{
		optionAction(action,option);
	}
	public void willNW(byte option) throws IOException{
		singleOptionNW(WILL,option);
	}
	public void doNW(byte option) throws IOException{
		singleOptionNW(DO,option);
	}
	public void wontNW(byte option) throws IOException{
		singleOptionNW(WONT,option);
	}
	public void dontNW(byte option) throws IOException{
		singleOptionNW(DONT,option);
	}

	//These wait for the response
	/* The non-IAC part of the input is discarded */
	/* could block for ever if the client does not understand */
	public void readUntilNextCommand() throws IOException{
		while( (byte)telnetRead(true)!=IAC);
	}
	protected boolean singleOption(byte action,byte option) throws IOException{
		optionAction(action,option);
		readUntilNextCommand();
		return getOptionStatus(option);
	}
	public boolean will(byte option) throws IOException{
		return singleOption(WILL,option);
	}
	public boolean do_(byte option) throws IOException{
		return singleOption(DO,option);
	}
	public boolean wont(byte option) throws IOException{
		return singleOption(WONT,option);
	}
	public boolean dont(byte option) throws IOException{
		return singleOption(DONT,option);
	}

	
	
	//Helpers to get terminal type
	public void negotiateTerminal() throws IOException{
		ByteVector b = new ByteVector();
		b.add((byte) 1); // SEND
		optionNegotiation(TERMINAL_TYPE,b);
	}
	
	/* could block for ever if the client does not understand */
	public String autoNegotiateTerminal() throws IOException{
		if(do_(TERMINAL_TYPE)){
			negotiateTerminal();
			readUntilNextCommand();
			
			ByteVector resp = getLastSubNegotiation(TERMINAL_TYPE);
			if(resp.firstElement()==(byte)0){ //IS
				StringBuilder bld = new StringBuilder();
				for(int n=1;n<resp.size();n++)
					bld.append((char)resp.elementAt(n).byteValue());
				return bld.toString().toLowerCase();
			}
		}
		return null;
	}
	
	
}
