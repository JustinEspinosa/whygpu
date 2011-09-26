package fun.useless.curses.application;


/**
 * 
 * @author justin
 *
 */
public abstract class ApplicationFactory {
	private Application singleInstance;
	protected void setSingleInstance(Application a){
		singleInstance = a;
	}
	protected Application singleInstance(){
		if(singleInstance!=null && !singleInstance.isStarted())
			return null;
		
		return singleInstance;
	}
	public abstract String getDisplayName();
	public abstract Application createInstance();
}
