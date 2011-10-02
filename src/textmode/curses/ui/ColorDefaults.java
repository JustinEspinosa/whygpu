package textmode.curses.ui;

import java.util.HashMap;
import java.util.Map;

public class ColorDefaults {
	
	private static ColorDefaults _def = new ColorDefaults();

	public static ColorPair getDefaultColor(ColorType t){
		return _def.get(t);
	}
	
	public static void setDefaultColor(ColorType t,ColorPair p){
		_def.put(t,p);
	}	
	
	
	private Map<ColorType,ColorPair> data = new HashMap<ColorType, ColorPair>();
	
	private ColorDefaults(){
		put(ColorType.MENU,    new ColorPair(AnsiColor8.Black,AnsiColor8.White)  );
		put(ColorType.WINDOW,  new ColorPair(AnsiColor8.Black,AnsiColor8.Yellow) );
		put(ColorType.SELECTED,new ColorPair(AnsiColor8.White,AnsiColor8.Blue)   );
		put(ColorType.TITLEBAR,new ColorPair(AnsiColor8.Yellow,AnsiColor8.Blue)  );
		put(ColorType.GREYED,  new ColorPair(AnsiColor8.Black,AnsiColor8.White)  );
		put(ColorType.BUTTON,  new ColorPair(AnsiColor8.Black,AnsiColor8.Cyan)   );
		put(ColorType.EDIT,    new ColorPair(AnsiColor8.Black,AnsiColor8.White)  );
		put(ColorType.DESKTOP, new ColorPair(AnsiColor8.Black,AnsiColor8.Black)  );		
	}
	
	private Map<ColorType,ColorPair> data(){
		return data;
	}
	
	private ColorPair get(ColorType t){
		return data().get(t);
	}
	
	private void put(ColorType t,ColorPair p){
		data().put(t,p);
	}
}
