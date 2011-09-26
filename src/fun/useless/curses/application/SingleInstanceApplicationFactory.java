package fun.useless.curses.application;

public abstract class SingleInstanceApplicationFactory extends
		ApplicationFactory {


	public abstract Application newInstance();
	
	@Override
	public final Application createInstance() {
		Application si = singleInstance();
		if(si == null){
			si = newInstance();
			setSingleInstance(si);
		}
		
		return si;
	}

}
