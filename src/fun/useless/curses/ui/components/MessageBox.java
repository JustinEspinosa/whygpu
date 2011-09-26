package fun.useless.curses.ui.components;

import fun.useless.curses.Curses;
import fun.useless.curses.application.Application;
import fun.useless.curses.ui.Dimension;
import fun.useless.curses.ui.Position;
import fun.useless.curses.ui.event.ActionEvent;
import fun.useless.curses.ui.event.ActionListener;


public class MessageBox extends ModalWindow {

	private static final int ButtonWidth = 8;
	
	public static enum Result{OK,YES,NO,CANCEL,NOTHING}
	public static enum ButtonType{OK,YES_NO_CANCEL}
	
	private Result myResult = Result.NOTHING;

	public MessageBox(String title,String text,ButtonType type, Application app,Curses cs) {
		super(title, app,cs, app.getWindowManager().getNextWindowPosition(), computeDimensions(text,type) );
	
		Label l = new Label(text,curses(),new Position(1,1),getSize().mutate(4, 2));
		intAddChild(l);
		
		Button bleft,bmid,bright;
		if(type==ButtonType.OK){
			bmid = new Button("Ok",curses(),new Position(buttonLine(),middle()),ButtonWidth);
			bmid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myResult = Result.OK;
					close();
				}
			});
			intAddChild(bmid);
		}else{
			bleft = new Button("Yes",curses(),new Position(buttonLine(),left()),ButtonWidth);
			bmid = new Button("No",curses(),new Position(buttonLine(),middle()),ButtonWidth);
			bright = new Button("Cancel",curses(),new Position(buttonLine(),right()),ButtonWidth);
			
			bleft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myResult = Result.YES;
					close();
				}
			});
			
			bmid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myResult = Result.NO;
					close();
				}
			});
			
			bright.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myResult = Result.CANCEL;
					close();
				}
			});
			
			intAddChild(bleft);
			intAddChild(bmid);
			intAddChild(bright);
		}
			
	}
	
	private int buttonLine(){
		return getSize().getLines()-2;
	}
	
	private int left(){
		return 1;
	}
	
	private int right(){
		return getSize().getCols()-ButtonWidth-1;
	}
	
	private int middle(){
		return (getSize().getCols()-ButtonWidth)/2;
	}
	
	private static Dimension computeDimensions(String text, ButtonType type) {
		int cols  = 0;
		String[] lines = text.split("\\n");
		
		for(String l:lines)  if(l.length()>cols)
			cols=l.length();
		
		cols+=2;
		
		int bCols = 0; 
		
		if(type==ButtonType.OK)
			bCols = ButtonWidth+2;
		if(type==ButtonType.YES_NO_CANCEL)
			bCols = (ButtonWidth+2)*3;
		
		return new Dimension(4 + lines.length ,Math.max(cols,bCols));
	}

	  
	public Result waitForChoice() throws InterruptedException{
		modalWait();
		return myResult;
	}
	
	public static void informUser(String title,String message, Application app, Curses cs){
		MessageBox msgb = new MessageBox(title, message, ButtonType.OK, app,cs);
		app.showWindow(msgb);
		
		try {
			msgb.waitForChoice();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
