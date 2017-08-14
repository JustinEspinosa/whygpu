package com.github.justinespinosa.textmode.data.sql;

import com.github.justinespinosa.textmode.data.Property;
import com.github.justinespinosa.textmode.data.PropertyList;

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
