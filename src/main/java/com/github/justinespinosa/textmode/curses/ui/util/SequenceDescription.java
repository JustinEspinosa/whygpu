package com.github.justinespinosa.textmode.curses.ui.util;


public class SequenceDescription{
	private int[] sequence;
	private int keyId;

	public SequenceDescription(String seq,int id){
		sequence = new int[seq.length()+1];
		int i=0; sequence[i++]=27;
		for(int n=0;n<seq.length();n++) sequence[i++]=seq.charAt(n);
		keyId = id;
	}
	public SequenceDescription(int[] seq,int id){
		sequence = seq;
		keyId = id;
	}
	public int[] getSequence(){
		return sequence;
	}
	public int getKeyId(){
		return keyId;
	}
	

}
