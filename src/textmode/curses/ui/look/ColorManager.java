package textmode.curses.ui.look;

import java.util.HashMap;
import java.util.Map;

import textmode.curses.ui.ColorDefaults;
import textmode.curses.ui.ColorPair;
import textmode.curses.ui.ColorType;
import textmode.curses.ui.components.AbstractMenu;
import textmode.curses.ui.components.AbstractTextField;
import textmode.curses.ui.components.AbstractWindow;
import textmode.curses.ui.components.Button;
import textmode.curses.ui.components.Component;
import textmode.curses.ui.components.LBLabel;
import textmode.curses.ui.components.Label;
import textmode.curses.ui.components.LineEdit;
import textmode.curses.ui.components.ListBox;
import textmode.curses.ui.components.MenuItem;
import textmode.curses.ui.components.MultiLineEdit;
import textmode.curses.ui.components.Panel;
import textmode.curses.ui.components.ScrollBar;
import textmode.curses.ui.components.Window;
import textmode.curses.ui.components.WindowPlane;


public class ColorManager {
	
	static final String STDCOL = "textmode.curses.ui.StdColor";
	static final String ALTCOL = "textmode.curses.ui.AltColor";
	public static final String TITLEBAR = "textmode.curses.ui.components.AbstractWindow.Titlebar";
	public static final String TITLEBAR_GRAYED = "textmode.curses.ui.components.AbstractWindow.Titlebar.Grayed";

	
	
	public static ColorManager createInstance(){
		
		ColorManager cm = new ColorManager(ColorDefaults.getDefaultColor(ColorType.WINDOW) );
		
		cm.set(ScrollBar.class,              ColorDefaults.getDefaultColor(ColorType.WINDOW ));
		cm.set(WindowPlane.class,            ColorDefaults.getDefaultColor(ColorType.DESKTOP));
		cm.set(Window.class,                 ColorDefaults.getDefaultColor(ColorType.WINDOW ));
		cm.set(Panel.class,                  ColorDefaults.getDefaultColor(ColorType.WINDOW ));
		cm.set(LineEdit.class,               ColorDefaults.getDefaultColor(ColorType.EDIT   ));
		cm.set(MultiLineEdit.class,          ColorDefaults.getDefaultColor(ColorType.EDIT   ));
		cm.set(AbstractTextField.class,      ColorDefaults.getDefaultColor(ColorType.EDIT   ));	
		cm.set(MenuItem.class,               ColorDefaults.getDefaultColor(ColorType.MENU   ));
		cm.set(ListBox.class,                ColorDefaults.getDefaultColor(ColorType.BUTTON ));
		cm.set(ListBox.BackgroundPanel.class,ColorDefaults.getDefaultColor(ColorType.BUTTON ));
		cm.set(Label.class,                  ColorDefaults.getDefaultColor(ColorType.WINDOW ));
		cm.set(LBLabel.class,                ColorDefaults.getDefaultColor(ColorType.BUTTON ));
		cm.set(Button.class,                 ColorDefaults.getDefaultColor(ColorType.BUTTON ));
		cm.set(AbstractMenu.class,           ColorDefaults.getDefaultColor(ColorType.MENU   ));
		cm.set(AbstractWindow.class,         ColorDefaults.getDefaultColor(ColorType.WINDOW ));
		
		cm.setAlt(MenuItem.class,ColorDefaults.getDefaultColor(ColorType.SELECTED ));
		cm.setAlt(LBLabel.class, ColorDefaults.getDefaultColor(ColorType.SELECTED ));
		
		cm.setCustom(AbstractWindow.class,TITLEBAR,       ColorDefaults.getDefaultColor(ColorType.TITLEBAR ));
		cm.setCustom(AbstractWindow.class,TITLEBAR_GRAYED,ColorDefaults.getDefaultColor(ColorType.GREYED   ));
		
		return cm;
	}
	
	
	private Map<Class<? extends Component>,Map<String,ColorPair>> _colors = new HashMap<Class<? extends Component>,Map<String, ColorPair>>();
	
	private ColorPair defaultColor;
	
	private ColorManager(ColorPair defCol){ defaultColor = defCol; }
	
	private void implSet(Class<? extends Component> key,String type,ColorPair value){
		Map<String,ColorPair> submap = _colors.get(key);
		if(submap==null){
			submap = new HashMap<String,ColorPair>();
			_colors.put(key,submap);
		}
		submap.put(type,value);
	}
	
	public void setCustom(Class<? extends Component> key, String type,ColorPair value){
		implSet(key,type, value);
	}
	
	public void set(Class<? extends Component> key,ColorPair value){
		implSet(key,STDCOL, value);
	}
	
	public void setAlt(Class<? extends Component> key,ColorPair value){
		implSet(key,ALTCOL, value);
	}

	@SuppressWarnings("unchecked")
	private ColorPair getImpl(String type, Class<? extends Component> key){
		
		while( (_colors.get(key) == null || _colors.get(key).get(type) == null) 
				&& key.getSuperclass()!=null
				&& Component.class.isAssignableFrom(key.getSuperclass()) )
			
			key = (Class<? extends Component>)key.getSuperclass();
		
		if(_colors.get(key)==null)
			return defaultColor;
		
		if(_colors.get(key).get(type)==null)
			return defaultColor;
		
		return  _colors.get(key).get(type);
	}
	
	public ColorPair getCustom(Class<? extends Component> key, String name){
		return getImpl(name,key);
	}
	
	public ColorPair getAlt(Class<? extends Component> key){
		return getImpl(ALTCOL,key);
	}
	
	public ColorPair get(Class<? extends Component> key){
		return getImpl(STDCOL,key);
	}
}
