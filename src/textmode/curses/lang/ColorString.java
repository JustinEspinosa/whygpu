package textmode.curses.lang;

import java.util.List;

public class ColorString {
	
	private List<ColorChar> source;
	
	
	protected ColorString(List<ColorChar> data){
		source = data;
	}
	
	public int length(){
		return source.size();
	}
	
	public ColorChar charAt(int index){
		return source.get(index);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(ColorChar cchar:source){
			builder.append(cchar.getChr());
		}
		return builder.toString();
	}
}
