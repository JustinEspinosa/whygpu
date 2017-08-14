package com.github.justinespinosa.textmode.data;

public class PListPersistenceException extends Exception {

	private static final long serialVersionUID = -5353853349371768245L;
	public PListPersistenceException(){
		super();
	}
	public PListPersistenceException(String message){
		super(message);
	}
	public PListPersistenceException(Exception cause){
		super(cause);
	}
	public PListPersistenceException(String message,Exception cause){
		super(message,cause);
	}
}
