package com.github.justinespinosa.textmode.curses.ui.event;

public class CharacterCodeEvent extends TerminalInputEvent {
	private char chr;
	/**
	 * 
	 * @param source usually an InputStream
	 * @param chr a character
	 */
	public CharacterCodeEvent(Object source, char c){
		super(source);
		chr = c;
	}
	/**
	 * 
	 * @return a character associated to this event
	 */
	public char getChar(){
		return chr;
	}
}
