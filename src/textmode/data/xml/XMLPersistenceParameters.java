package textmode.data.xml;

import textmode.data.Property;
import textmode.data.PropertyList;

public class XMLPersistenceParameters extends PropertyList {
	
	public XMLPersistenceParameters(){
	}

	public XMLPersistenceParameters(String path){
		pathName = path;
	}
	
	@Property(persistant=true)
	public String pathName = "";
}
