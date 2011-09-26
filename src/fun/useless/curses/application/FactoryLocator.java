package fun.useless.curses.application;

import java.util.Map;
import java.util.HashMap;

public class FactoryLocator {
	
	private Map<Class<? extends Application>,ApplicationFactory>  factories = new HashMap<Class<? extends Application>, ApplicationFactory>();
	
	public void register(Class<? extends Application> app, ApplicationFactory factory){
		factories.put(app, factory);
	}
	
	public ApplicationFactory locate(Class<? extends Application> app){
		return factories.get(app);
	}
	
}
