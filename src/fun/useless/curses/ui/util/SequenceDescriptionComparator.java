package fun.useless.curses.ui.util;

import java.util.Comparator;

public class SequenceDescriptionComparator implements Comparator<SequenceDescription> {

	public int compare(SequenceDescription arg0, SequenceDescription arg1) {
		int[] seq0 = arg0.getSequence();
		int[] seq1 = arg1.getSequence();
		
		if(seq0.length > seq1.length){
			for(int n=0;n<seq1.length;n++){
				if(seq0[n] > seq1[n])
					return 1;
				if(seq0[n] < seq1[n])
					return -1;
			}
			return 1;
		}
		
		if(seq0.length < seq1.length){
			for(int n=0;n<seq0.length;n++){
				if(seq0[n] > seq1[n])
					return 1;
				if(seq0[n] < seq1[n])
					return -1;
			}
			return -1;
		}
		
		for(int n=0;n<seq0.length;n++){
			if(seq0[n] > seq1[n])
				return 1;
			if(seq0[n] < seq1[n])
				return -1;
		}

		return 0;
	}

}
