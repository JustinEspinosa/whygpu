package com.github.justinespinosa.textmode.curses.ui;


public enum AnsiColor8 implements Color {
	Black   (0x00,0x00,0x00),
	Red     (0x80,0x00,0x00),
	Green   (0x00,0x80,0x00),
	Yellow  (0x80,0x80,0x00),
	Blue    (0x00,0x00,0x80),
	Magenta (0x80,0x00,0x80),
	Cyan    (0x00,0x80,0x80),
	White   (0xc0,0xc0,0xc0);
	
	private RGB color;
	private AnsiColor8(int r,int g,int b){
		color = new RGB(r,g,b,ordinal());
	}
	
	public int index(){
		return color.index();
	}
	
	public RGB rgb() {
		return color;
	}
	
	public ColorDepth depth(){
		return ColorDepth.COL8;
	}
	
	
}
