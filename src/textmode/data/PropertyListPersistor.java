package textmode.data;

public abstract class PropertyListPersistor<T extends PropertyList> {	
	
	private PropertyList parameters;
	public PropertyListPersistor(PropertyList params){
		parameters = params;
	}
	
	protected PropertyList params(){
		return parameters;
	}
	public abstract void write(T list) throws PListPersistenceException;
	public abstract T read(T list) throws PListPersistenceException;
}
