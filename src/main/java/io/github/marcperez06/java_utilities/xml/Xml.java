/**
 * @author Marc Perez Rodriguez
 */
package io.github.marcperez06.java_utilities.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.logger.Logger;

public class Xml {

	public static final String FILE_SOURCE = "file";
    public static final String URL_SOURCE = "url";
    
	private Document document;
	
	public Xml() {
		try {
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			this.document = null;
			e.printStackTrace();
		}
	}
	
	public Xml(String typeSoure, String path) {
        if (typeSoure.equals(FILE_SOURCE) == true) {
            this.document = this.getDocumentFromFile(path);
        } else if (typeSoure.equals(URL_SOURCE) == true) {
            this.document = this.getDocumentFromUrl(path);
        } else {
            this.document = null;
        }
    }
	
	public Document getDocument() {
		return this.document;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	private Document getDocumentFromFile(String path) {
        Document doc = null;
        File file = new File(path);
        if (file != null) {
            try {
                doc = this.getDocumentFromUrl(file.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                doc = null;
                e.printStackTrace();
            }
        }
        return doc;
    }

    private Document getDocumentFromUrl(String urlString) {
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
	
	private boolean haveDocument() {
		return this.document != null;
	}
	
	public Element addNodeWithValue(String nodeName, String nodeValue) {
		Element element = null;
		if (this.haveDocument() == true) {
			element = this.addNode(nodeName);
			
			if (element != null) {
				element.setNodeValue(nodeValue);
			}
		}
		return element;
	}
	
	public Element addNode(String nodeName) {
		Element element = null;
		if (this.haveDocument() == true) {
			element = this.createNode(nodeName);
			this.document.appendChild(element);
		}
		return element;
	}
	
	public Element addChildNode(String parentNode, String childNode) {
		Element child = null;
		if (this.haveDocument() == true) {
			NodeList nodeList = this.document.getChildNodes();
			List<Node> nodes = this.getNodes(nodeList, parentNode);
			if (nodes.size() > 0) {
				for (Node node : nodes) {
					child = this.createNode(childNode);
					node.appendChild(child);
				}
			}
		}
		return child;
	}
	
	public Element createNode(String name) {
		return this.document.createElement(name);
	}
	
	public void addNodeAttribute(Element node, String attributeName, String attributeValue) {
		if (node != null) {
			node.setAttribute(attributeName, attributeValue);
		}
	}
	
	public List<Node> getNodes(NodeList list, String nodeName) {
		List<Node> nodes = new ArrayList<Node>();
		for (int i  = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (nodeName.equals(node.getNodeName()) == true) {
				nodes.add(node);
			} else if (node.hasChildNodes() == true) {
				List<Node> auxNodes = this.getNodes(node.getChildNodes(), nodeName);
				if (auxNodes.size() > 0) {
					nodes.addAll(auxNodes);
				}
			}
		}
		return nodes;
	}
	
	public Node getFirstNodeByTagName(String tagName) {
        Node node = null;
        NodeList nodeList = this.getNodeListByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0)  {
            node = nodeList.item(0);
        }
        return node;
    }

    public NodeList getNodeListByTagName(String tagName) {
        NodeList nodeList = null;
        if (this.haveDocument() == true) {
            nodeList = this.document.getElementsByTagName(tagName);
        }
        return nodeList;
    }

    public List<Node> getNodesByTagName(String tagName) {
        List<Node> list= new ArrayList<Node>();
        if (this.haveDocument() == true) {
            NodeList nodeList = this.document.getChildNodes();
            List<Node> listOfNodes = this.getListOfNodesByTagNameFromNodeList(nodeList, tagName);
            if (listOfNodes.size() > 0) {
                list.addAll(listOfNodes);
            }
        }
        return list;
    }

    private List<Node> getListOfNodesByTagNameFromNodeList(NodeList nodeList, String tagName) {
        List<Node> list= new ArrayList<Node>();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String nodeName = node.getNodeName();
                List<Node> aux = this.getListOfNodesByTagNameFromNodeList(node.getChildNodes(), tagName);

                if (nodeName.equals(tagName) == true) {
                    this.addNodeInList(list, node);
                }

                if (aux.size() > 0) {
                    list.addAll(aux);
                }
            }
        }
        return list;
    }
    
    private void addNodeInList(List<Node> list, Node node) {
        boolean canAdd = true;

        if (node.getNodeType() == Node.TEXT_NODE) {
            String nodeValue = node.getNodeValue();
            canAdd = !nodeValue.contains("\n");
            canAdd &= !nodeValue.contains("\t");
        }

        if (canAdd) {
            list.add(node);
        }
    }

    public String getChildNodeValue(Node parent, String childName) {
        boolean founded = false;
        String value = "";
        if (parent != null) {
            NodeList childNodes = parent.getChildNodes();
            for (int i = 0; i < childNodes.getLength() && founded == false; i++) {
                Node childNode = childNodes.item(i);
                if (childName.equals(childNode.getNodeName()) == true) {
                    value = childNode.getTextContent();
                    founded = true;
                }
            }
        }
        return value;
    }

    public List<Node> getAllNodesWhoseParentIs(String parentName) {
        List<Node> list = new ArrayList<Node>();
        if (this.haveDocument() == true) {
            NodeList nodeList = this.document.getChildNodes();
            List<Node> listOfNodes = this.getListOfNodesByParentNameFromNodeList(nodeList, parentName);
            if (listOfNodes.size() > 0) {
                list.addAll(listOfNodes);
            }
        }
        return list;
    }

    private List<Node> getListOfNodesByParentNameFromNodeList(NodeList nodeList, String parentName) {
        List<Node> list= new ArrayList<Node>();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node parentNode = node.getParentNode();
                if (parentNode != null) {
                    String nameOfParent = parentNode.getNodeName();
                    List<Node> aux = this.getListOfNodesByParentNameFromNodeList(node.getChildNodes(), parentName);

                    if (nameOfParent.equals(parentName) == true) {
                        this.addNodeInList(list, node);
                    }

                    if (aux.size() > 0) {
                        list.addAll(aux);
                    }
                }
            }
        }
        return list;
    }
	
	public List<Node> getNodeByName(String nodeName) {
		List<Node> nodes = new ArrayList<Node>();
		
		if (this.haveDocument() == true) {
			NodeList nodeList = this.document.getChildNodes();
			nodes = this.getNodes(nodeList, nodeName);
		}
		
		return nodes;
	}
	
	public List<Node> getChildNodesByName(String parentName, String nodeName) {
		List<Node> parentNodes = this.getNodeByName(parentName);
		List<Node> nodes = new ArrayList<Node>();
		if (parentNodes.size() > 0) {
			for (int i = 0; i < parentNodes.size(); i++) {
				NodeList list = parentNodes.get(i).getChildNodes();
				nodes.addAll(this.getNodes(list, nodeName));
			}
		}
		
		return nodes;
	}
	
	public Node getNodeByPos(int pos) {
		Node node = null;
		if (this.haveDocument() == true && pos >= 0) {
			NodeList nodeList = this.document.getChildNodes();
			node = nodeList.item(pos);
		}
		return node;
	}
	
	public Node getChildNodeByPos(String parentName, int pos) {
		List<Node> parentNodes = this.getNodeByName(parentName);
		Node node = null;
		if (parentNodes.size() > 0) {
			NodeList list = parentNodes.get(0).getChildNodes();
			node = list.item(0);
		}
		
		return node;
	}
	
	public boolean haveChildren(Node node) {
		return !this.getChildNodes(node).isEmpty();
	}
	
	public List<Node> getChildNodes(Node node) {
		List<Node> children = new ArrayList<Node>();
		if (node != null && node.hasChildNodes()) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				this.addNodeInList(children, item);
			}
		}
		return children;
	}
	
	public List<Node> getNodesByAttribute(String attributeName, String attributeValue) {
        List<Node> list= new ArrayList<Node>();
        if (this.haveDocument() == true) {
            NodeList nodeList = this.document.getChildNodes();
            List<Node> listOfNodes = this.getListOfNodesByAttributeFromNodeList(nodeList, attributeName, attributeValue);
            if (listOfNodes.size() > 0) {
                list.addAll(listOfNodes);
            }
        }
        return list;
    }

    private List<Node> getListOfNodesByAttributeFromNodeList(NodeList nodeList, String attributeName, String attributeValue) {
        List<Node> list= new ArrayList<Node>();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
            	List<Node> aux = new ArrayList<Node>();
                Node node = nodeList.item(i);
                String nodeAttribute = this.getNodeAttribute(node, attributeName);
                
                if (node.hasChildNodes()) {
                	aux = this.getListOfNodesByAttributeFromNodeList(node.getChildNodes(), attributeName, attributeValue);	
                }

                if (attributeValue.equals(nodeAttribute)) {
                    this.addNodeInList(list, node);
                }

                if (aux.isEmpty()) {
                    list.addAll(aux);
                }
            }
        }
        return list;
    }
    
    public String getNodeAttribute(Node node, String attributeName) {
    	boolean founded = false;
		String attributeValue = "";
		NamedNodeMap attributesMap = node.getAttributes();
		
		for (int i = 0; i < attributesMap.getLength() && !founded; i++) {
			Node attribute = attributesMap.item(i);
			if (attributeName.equals(attribute.getNodeName())) {
				attributeValue = attribute.getTextContent();
				founded = true;
			}
		}
		
		return attributeValue;
    }
    
    public String getNodeTag(Node node) {
		String tag = null;
		if (node != null) {
			tag = node.getNodeName();
		}
		return tag;
	}
	
	public String getNodeValue(Node node) {
		String value = null;
		if (node != null) {
			
			try {
				if (node.getNodeType() == Node.TEXT_NODE) {
					value = node.getTextContent();
				} else {
					value = node.getNodeValue();
				}
			} catch (Exception e) { }

		}
		return value;
	}
	
	// ******** Transformations *************
	
	public <V> String toJson() {
		HashMap<String, V> map = this.toMap();
		return GsonUtils.getJSON(map);
	}
	
	public <V> HashMap<String, V> toMap() {
		List<HashMap<String, V>> list = this.xmlToMapList();
		HashMap<String, V> map = new HashMap<String, V>();
		if (!list.isEmpty()) {
			map = list.get(0);
		}
		return map;
	}
	
	public <V> List<HashMap<String, V>> xmlToMapList() {
		List<HashMap<String, V>> list = new ArrayList<HashMap<String, V>>();
		HashMap<String, V> map = new HashMap<String, V>();
		
		if (this.haveDocument()) {
			NodeList nodes = this.document.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				String key = this.getNodeTag(item);
				HashMap<String, V> value = this.toMapFromNode(item);
				map.put(key, (V) value);
				list.add(map);
			}
		}
		return list;
	}
	
	private <V> HashMap<String, V> toMapFromNode(Node node) {
		HashMap<String, V> map = new HashMap<String, V>();

		if (node != null) {

			List<Node> nodes = this.getChildNodes(node);
			
			if (!nodes.isEmpty()) {
				
				for (int i = 0; i < nodes.size(); i++) {
					Node item = nodes.get(i);
					this.addNodeToMap(item, map);
				}

			} else {
				this.addNodeToMap(node, map);
			}
		}
		return map;
	}
	
	private <V> void addNodeToMap(Node node, HashMap<String, V> map) {
		String key = this.getNodeTag(node);
		
		if (this.haveChildren(node)) {
			HashMap<String, V> value = this.toMapFromNode(node);
			if (value.containsKey("#text")) {
				// Delete node #text level
				String nodeValue = (String) value.get("#text");
				map.put(key, (V) nodeValue);
			} else {
				map.put(key, (V) value);	
			}
		} else {
			key = this.getNodeTag(node);
			String value = this.getNodeValue(node);
			map.put(key, (V) value);
		}
	}
    
    @Override
	public String toString() {
		String str = "";
		if (this.haveDocument() == true) {
			str = this.document.getTextContent();
		}
		return str;
	}
	
}