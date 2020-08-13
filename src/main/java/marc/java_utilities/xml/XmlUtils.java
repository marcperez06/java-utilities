/**
 * @author Marc Perez Rodriguez
 */
package marc.java_utilities.xml;

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
	
	private XmlUtils() {}

	public static Document getXmlDocumentFromFile(String path) {
		Document doc = null;
		File file = new File(path);
		if (file != null) {
			try {
				doc = getXmlDocumentFromUrl(file.toURI().toURL().toString());
			} catch (MalformedURLException e) {
				doc = null;
				e.printStackTrace();
			}
		}
		return doc;
	}
	
	public static Document getXmlDocumentFromUrl(String urlString) {
		Document doc = null;

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(conn.getInputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	public static NodeList getNodeListFromUrl(String url, String nodeName) {
		NodeList nodeList = null;
		Document doc = getXmlDocumentFromUrl(url);

		if (doc != null) {
			nodeList = doc.getElementsByTagName(nodeName);
		}

		return nodeList;
	}

	public static String getNodeValueFromUrl(String url, String nodeName) {
		String value = "";
		List<String> valueList = getNodesValueFromUrl(url, nodeName);
		if (valueList.size() > 0) {
			value = valueList.get(0);
		}
		return value;
	}

	public static List<String> getNodesValueFromUrl(String url, String nodeName) {
		NodeList nodeList = getNodeListFromUrl(url, nodeName);
		List<String> valueList = getNodesValue(nodeList);
		return valueList;
	}
	
	public static String getNodeValue(Node node) {
		String nodeValue = null;
		List<String> nodeValues = getNodeValues(node);
		
		if (nodeValues.size() > 0) {
			nodeValue = nodeValues.get(0).trim().replaceAll("\n", "");
		}
		
		return nodeValue;
	}

	public static List<String> getNodesValue(NodeList nodeList) {
		List<String> valueList = new ArrayList<String>();

		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				List<String> auxList = getNodeValues(node);
				valueList.addAll(auxList);
			}
		}

		return valueList;
	}

	public static List<String> getNodeValues(Node node) {
		String nodeValue = "";
		List<String> valueList = new ArrayList<String>();

		if (node != null) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				nodeValue = node.getTextContent();
				if (nodeValue.isEmpty() == false) {
					valueList.add(nodeValue);
				}
			} else {
				NodeList childNodes = node.getChildNodes();
				List<String> childValueList = getNodesValue(childNodes);
				valueList.addAll(childValueList);
			}
		}

		return valueList;
	}

	public static Map<String, List<String>> getTextNodesValuesFromUrl(String url) {
		Document document = getXmlDocumentFromUrl(url);
		NodeList nodeList = document.getChildNodes();
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		fillMapWithNodeList(nodeList, map);
		return map;
	}
	
	public static Map<String, List<String>> getTextNodesValuesFromFile(String path) {
		Document document = getXmlDocumentFromFile(path);
		NodeList nodeList = document.getChildNodes();
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		fillMapWithNodeList(nodeList, map);
		return map;
	}

	private static void fillMapWithNodeList(NodeList nodeList, Map<String, List<String>> map) {
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.TEXT_NODE) {
					fillMapWithTextNodesValue(node, map);
				} else {
					NodeList childNodes = node.getChildNodes();
					fillMapWithNodeList(childNodes, map);
				}

			}
		}
	}

	private static void fillMapWithTextNodesValue(Node node, Map<String, List<String>> map) {
		if (node != null) {
			String parentName = node.getParentNode().getNodeName();
			String nodeValue = getNodeValue(node);
			List<String> nodeValues = map.get(parentName);
			if (nodeValues != null && nodeValue.isEmpty() == false) {
				nodeValues.add(nodeValue);
			} else {
				List<String> newNodeValues = new ArrayList<String>();
				if (nodeValue.isEmpty() == false) {
					newNodeValues.add(nodeValue);
				}
				map.put(parentName, newNodeValues);
			}
		}
	}
	
	public static String getNodeAttribute(Node node, String attributeName) {
		boolean founded = false;
		String attributeValue = "";
		NamedNodeMap attributesMap = node.getAttributes();
		
		for (int i = 0; i < attributesMap.getLength() && founded == false; i++) {
			Node attribute = attributesMap.item(i);
			if (attributeName.equals(attribute.getNodeName()) == true) {
				attributeValue = attribute.getTextContent();
				founded = true;
			}
		}
		
		return attributeValue;
	}
	
	public static Node getFirstChildNodeByName(Node parentNode, String childName) {
		boolean founded = false;
		Node childNode = null;
		
		NodeList nodeList = parentNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength() && founded == false; i++) {
			Node currentChild = nodeList.item(i);
			
			if (currentChild.getNodeName().equals(childName) == true) {
				childNode = currentChild;
				founded = true;
			} else if (currentChild.getChildNodes().getLength() > 0) {
				Node auxNode = getFirstChildNodeByName(currentChild, childName);
				if (auxNode != null) {
					childNode = auxNode;
					founded = true;
				}
			}
		}
		
		return childNode;
	}
	
	public static String convertDocumentToString(Document doc) {
		String output = null;
		Transformer transformer = createTransformerWithoutXmlDeclaration();
        StringWriter writer = new StringWriter();
        
        if (transformer != null) {
	        
        	try {
        		DOMSource domSource = new DOMSource(doc);
        		StreamResult streamResult = new StreamResult(writer);
				transformer.transform(domSource, streamResult);
				output = writer.getBuffer().toString();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        	
		}

        return output;
    }
	
	private static Transformer createTransformerWithoutXmlDeclaration() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
			transformer = transformerFactory.newTransformer();
			// below code to remove XML declaration
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));
		} catch (TransformerConfigurationException e) {
			transformer = null;
			e.printStackTrace();
		}
        return transformer;        
	}
	
	public static String generateHtmlUsingXslt(String xmlPath, String xsltPath) {
		String html = "";
		File xmlFile = new File(xmlPath);
		File xsltFile = new File(xsltPath);
		StringWriter writer = new StringWriter();
		StreamResult streamResult = new StreamResult(writer);
		
		if (xmlFile.exists() && xsltFile.exists()) {
			Source xml = new StreamSource(xmlFile);
			Source xslt = new StreamSource(xsltFile);
			
			try {
				TransformerFactory transformFactory = TransformerFactory.newInstance();
				Transformer trasform = transformFactory.newTransformer(xslt);
				trasform.transform(xml, streamResult);
				html = writer.toString();
			} catch (TransformerException e) {
				html = "";
				e.printStackTrace();
			}
		}
		
		return html;
	}

}