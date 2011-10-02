package textmode.data.xml;

import textmode.data.Property;
import textmode.data.PropertyList;

public class XMLPersistenceParameters extends PropertyList {
	
	@Property(persistant=true)
	public String pathName;
}
