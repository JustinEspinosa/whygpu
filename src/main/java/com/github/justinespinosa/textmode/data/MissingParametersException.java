package com.github.justinespinosa.textmode.data;

public class MissingParametersException extends RuntimeException {

	private static final long serialVersionUID = -3897879427231159285L;
	public MissingParametersException(){
		super();
	}
	public MissingParametersException(String message){
		super(message);
	}
}
