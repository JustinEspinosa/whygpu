package fun.useless.curses.ui;

import java.util.HashMap;
import java.util.Map;

import fun.useless.curses.Curses;

public class ColorDefaults {
	
	public static enum ColorDepth{
		COL8,
		COL16,
		COL256
	}
	
	private static ColorDefaults _def = new ColorDefaults();

	public static ColorPair getDefaultColor(ColorType t, Curses cs){
		return getDefaultColor(t, getColDepth(cs) );
	}
	
	public static ColorPair getDefaultColor(ColorType t,ColorDepth cd){
		return _def.get(t,cd);
	}
	
	public static ColorDepth getColDepth(Curses cs){
		if(cs.numcolors()<16)
			return ColorDepth.COL8;
		
		if(cs.numcolors()<88)
			return ColorDepth.COL16;
		
		return ColorDepth.COL256;		
	}
	
	public static void setDefaultColor(ColorType t,ColorPair p, ColorDepth cd){
		//System.out.println(t+"=>"+p);
		_def.put(t,p,cd);
	}	
	
	
	private Map<ColorType,ColorPair> data8 = new HashMap<ColorType, ColorPair>();
	private Map<ColorType,ColorPair> data16 = new HashMap<ColorType, ColorPair>();
	private Map<ColorType,ColorPair> data256 = new HashMap<ColorType, ColorPair>();
	
	private ColorDefaults(){
		put(ColorType.MENU,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL8);
		put(ColorType.WINDOW,new ColorPair(AnsiColor8.Black,AnsiColor8.Yellow),ColorDepth.COL8);
		put(ColorType.SELECTED,new ColorPair(AnsiColor8.White,AnsiColor8.Blue),ColorDepth.COL8);
		put(ColorType.TITLEBAR,new ColorPair(AnsiColor8.Yellow,AnsiColor8.Blue),ColorDepth.COL8);
		put(ColorType.GREYED,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL8);
		put(ColorType.BUTTON,new ColorPair(AnsiColor8.Black,AnsiColor8.Cyan),ColorDepth.COL8);
		put(ColorType.EDIT,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL8);
		put(ColorType.DESKTOP,new ColorPair(AnsiColor8.Black,AnsiColor8.Black),ColorDepth.COL8);

		put(ColorType.MENU,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL16);
		put(ColorType.WINDOW,new ColorPair(AnsiColor8.Black,AnsiColor8.Yellow),ColorDepth.COL16);
		put(ColorType.SELECTED,new ColorPair(AnsiColor8.White,AnsiColor8.Blue),ColorDepth.COL16);
		put(ColorType.TITLEBAR,new ColorPair(AnsiColor8.Yellow,AnsiColor8.Blue),ColorDepth.COL16);
		put(ColorType.GREYED,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL16);
		put(ColorType.BUTTON,new ColorPair(AnsiColor8.Black,AnsiColor8.Cyan),ColorDepth.COL16);
		put(ColorType.EDIT,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL16);
		put(ColorType.DESKTOP,new ColorPair(AnsiColor8.Black,AnsiColor8.Black),ColorDepth.COL16);
		
		put(ColorType.MENU,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL256);
		put(ColorType.WINDOW,new ColorPair(AnsiColor8.Black,AnsiColor8.Yellow),ColorDepth.COL256);
		put(ColorType.SELECTED,new ColorPair(AnsiColor8.White,AnsiColor8.Blue),ColorDepth.COL256);
		put(ColorType.TITLEBAR,new ColorPair(AnsiColor8.Yellow,AnsiColor8.Blue),ColorDepth.COL256);
		put(ColorType.GREYED,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL256);
		put(ColorType.BUTTON,new ColorPair(AnsiColor8.Black,AnsiColor8.Cyan),ColorDepth.COL256);
		put(ColorType.EDIT,new ColorPair(AnsiColor8.Black,AnsiColor8.White),ColorDepth.COL256);
		put(ColorType.DESKTOP,new ColorPair(AnsiColor8.Black,AnsiColor8.Black),ColorDepth.COL256);		
	}
	
	private Map<ColorType,ColorPair> data(ColorDepth cd){
		switch(cd){
		case COL256:
			return data256;
		case COL16:
			return data16;
		case COL8:
		default:
			return data8;
		}
	}
	
	private ColorPair get(ColorType t,ColorDepth cd){
		return data(cd).get(t);
	}
	
	private void put(ColorType t,ColorPair p,ColorDepth cd){
		data(cd).put(t,p);
	}
}
