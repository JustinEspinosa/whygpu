package fun.useless.curses.application;


/**
 * 
 * @author justin
 *
 */
public abstract class ApplicationFactory {
	public abstract String getDisplayName();
	public abstract Application createInstance();
}
