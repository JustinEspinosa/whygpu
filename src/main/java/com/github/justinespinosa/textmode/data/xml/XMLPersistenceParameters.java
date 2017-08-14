package com.github.justinespinosa.textmode.data.xml;

import com.github.justinespinosa.textmode.data.Property;
import com.github.justinespinosa.textmode.data.PropertyList;

public class XMLPersistenceParameters extends PropertyList {
	
	public XMLPersistenceParameters(){
	}

	public XMLPersistenceParameters(String path){
		pathName = path;
	}
	
	@Property(persistant=true)
	public String pathName = "";
}
