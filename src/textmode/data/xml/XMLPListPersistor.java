package textmode.data.xml;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import textmode.data.PListPersistenceException;
import textmode.data.PropertyList;
import textmode.data.PropertyListPersistor;


/**
 * For small amount of data.
 * 
 * @author justin
 *
 * @param <T>
 */
public class XMLPListPersistor<T extends PropertyList> extends PropertyListPersistor<T> {
	
	
	public static <T extends PropertyList> XMLPListPersistor<T> fromParameterFile(String pathName, Class<T> type){
		XMLPersistenceParameters params = new XMLPersistenceParameters();
		params.set("pathName", pathName);
		
		return new XMLPListPersistor<T>(params);
	}

	private Document dataDocument = null;
	private File     diskFile;
	private DocumentBuilder builder = null;
	private DocumentBuilderFactory bFactory;
	private TransformerFactory tFactory;
	private Transformer transformer = null;
	private XMLPListNodeFactory nodeFactory;
	private SortedMap<PropertyList,Node> _cache;
	
	
	public XMLPListPersistor(PropertyList params) {
		super(params);
		diskFile = new File(params.get("pathName", String.class));
		bFactory = DocumentBuilderFactory.newInstance();
		tFactory = TransformerFactory.newInstance();
	}
	
	private void ensureBuilderExists() throws ParserConfigurationException{
		if(builder==null)
			builder = bFactory.newDocumentBuilder();
	}

	private void loadDocument() throws ParserConfigurationException, SAXException, IOException{
		ensureBuilderExists();
		boolean exists = diskFile.exists();
		
		if(exists)
			dataDocument = builder.parse(diskFile);
		else
			dataDocument = builder.newDocument();
		
		nodeFactory = new XMLPListNodeFactory(dataDocument);
		
		if(exists)
			readDocument();
		else
			setupDocument();
	}
	
	private void ensureDocumentExists() throws ParserConfigurationException, SAXException, IOException{
		if(dataDocument==null)
			loadDocument();
	}
	
	private void setupDocument(){
		nodeFactory.setup();
		readDocument();
	}
	
	private void readDocument(){
		XMLPListNodeReader reader = new XMLPListNodeReader(dataDocument);
		_cache = reader.readDocument();
	}
	
	private void ensureTransformerExists() throws TransformerException{
		if(transformer==null){
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
	}
	
	private void writeDocument() throws TransformerException{
		ensureTransformerExists();
		
		nodeFactory.cleanUp();
		DOMSource source = new DOMSource(dataDocument);
		StreamResult result = new StreamResult(diskFile);
		transformer.transform(source, result);
	}
	
	@Override
	public void write(T list) throws PListPersistenceException{
		try {
			ensureDocumentExists();
			Node n = _cache.get(list);
			Node parent = n.getParentNode();
			parent.removeChild(n);
			n = nodeFactory.createNode(null, list);
			parent.appendChild(n);
			_cache.put(list, n);
			writeDocument();
		} catch (ParserConfigurationException e) {
			throw new PListPersistenceException(e);
		} catch (SAXException e) {
			throw new PListPersistenceException(e);
		} catch (IOException e) {
			throw new PListPersistenceException(e);
		} catch (TransformerException e) {
			throw new PListPersistenceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(T list) throws PListPersistenceException{
		try {
			ensureDocumentExists();
			PropertyList storedList = _cache.tailMap(list).entrySet().iterator().next().getKey();
			
			return (T) storedList;
			
		} catch (ParserConfigurationException e) {
			throw new PListPersistenceException(e);
		} catch (SAXException e) {
			throw new PListPersistenceException(e);
		} catch (IOException e) {
			throw new PListPersistenceException(e);
		} catch (NoSuchElementException e){
			
			Node n = nodeFactory.createNode(null, list);
			dataDocument.getFirstChild().appendChild(n);
			_cache.put(list, n);
			return list;
			
		}
	}

}
