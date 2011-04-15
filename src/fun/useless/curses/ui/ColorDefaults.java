package fun.useless.curses.ui;

import java.util.HashMap;

public class ColorDefaults {
	private static ColorDefaults _def = new ColorDefaults();

	public static ColorPair getDefaultColor(ColorType t){
		return _def.get(t);
	}
	public static void setDefaultColor(ColorType t,ColorPair p){
		_def.put(t,p);
	}	
	
	private HashMap<ColorType,ColorPair> data = new HashMap<ColorType, ColorPair>();
	
	private ColorDefaults(){
		put(ColorType.MENU,new ColorPair(0,7));
		put(ColorType.WINDOW,new ColorPair(0,3));
		put(ColorType.SELECTED,new ColorPair(7,4));
		put(ColorType.TITLEBAR,new ColorPair(3,4));
		put(ColorType.BUTTON,new ColorPair(0,6));
		put(ColorType.EDIT,new ColorPair(0,7));
		put(ColorType.DESKTOP,new ColorPair(0,0));
	}
	
	private ColorPair get(ColorType t){
		return data.get(t);
	}
	
	private void put(ColorType t,ColorPair p){
		data.put(t,p);
	}
}
