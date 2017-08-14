package com.github.justinespinosa.textmode.curses.ui;

import java.util.Collections;

import com.github.justinespinosa.textmode.curses.ui.event.TermKeyEvent;
import com.github.justinespinosa.textmode.curses.ui.util.SequenceDescription;
import com.github.justinespinosa.textmode.curses.ui.util.SequenceDescriptionComparator;
import com.github.justinespinosa.textmode.curses.ui.util.SortedList;



public final class EscapeDetector{
	private SequenceDescriptionComparator comp = new SequenceDescriptionComparator();
	private SortedList<SequenceDescription> data = new SortedList<SequenceDescription>(comp);
	
	EscapeDetector(){
		buildDB();
	}
	private void buildDB(){
		data.add(new SequenceDescription("[A",TermKeyEvent.UP_ARROW ));
		data.add(new SequenceDescription("[B",TermKeyEvent.DOWN_ARROW ));
		data.add(new SequenceDescription("[D",TermKeyEvent.LEFT_ARROW ));
		data.add(new SequenceDescription("[C",TermKeyEvent.RIGHT_ARROW ));
		data.add(new SequenceDescription("OP",TermKeyEvent.F1));
		data.add(new SequenceDescription("OQ",TermKeyEvent.F2));
		data.add(new SequenceDescription("OR",TermKeyEvent.F3));
		data.add(new SequenceDescription("OS",TermKeyEvent.F4));
		data.add(new SequenceDescription("x",TermKeyEvent.EXIT));
		data.add(new SequenceDescription("p",TermKeyEvent.MOVE));		
		data.add(new SequenceDescription("r",TermKeyEvent.RESIZE));		
		data.add(new SequenceDescription("m",TermKeyEvent.MENU));	
		data.add(new SequenceDescription("w",TermKeyEvent.CLOSE));
		data.add(new SequenceDescription("n",TermKeyEvent.NEW));
		data.add(new SequenceDescription("v",TermKeyEvent.NEXT));
		data.add(new SequenceDescription("s",TermKeyEvent.SCROLL));
		data.add(new SequenceDescription(new int[]{27,27},TermKeyEvent.CANCEL));
		data.add(new SequenceDescription(new int[]{27,9},TermKeyEvent.NEXTAPP));
	}
	/* know in h is a seq or could be the beginning of one*/
	public int countMatches(int[] h){
		SequenceDescription d=new SequenceDescription(h,-1);
		int n  = Collections.binarySearch(data, d, comp);
		
		if(n>-1)
			return 1;
		
		n=(-1*n)-1;

		if(data.size()<=n)
			return 0;
		
		int[] s1 = d.getSequence(); 
		int[] s2 = data.get(n).getSequence();
		
		
		if(s1.length>s2.length)
			return 0;
		
        for(int c=0;c<s1.length;c++) if(s1[c]!=s2[c])
        	return 0;
        
		return 2;
	}
	public int getMatch(int[] h){
		SequenceDescription d=new SequenceDescription(h,-1);
		int n  = Collections.binarySearch(data, d, comp);
		if(n<0)
			return -1;
		return data.get(n).getKeyId();
	}
}