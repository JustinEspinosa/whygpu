package textmode.data.sql;

import textmode.data.Property;
import textmode.data.PropertyList;

public class SQLPersistenceParameters extends PropertyList {

	public SQLPersistenceParameters(){
	}
	
	public SQLPersistenceParameters(String conn){
		connectionString= conn;
	}
	
	@Property(persistant=true)
	public String connectionString = "";
	
	@Property(persistant=true)
	public String driver = "";
	
	@Property(persistant=true)
	public String user = "";
	
	@Property(persistant=true)
	public String password = "";
	
}
