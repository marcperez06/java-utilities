/**
 * @author Marc Perez Rodriguez
 */
package io.github.marcperez06.java_utilities.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	@Override
	public String toString() {
		String str = "";
		if (this.haveDocument() == true) {
			str = this.document.getTextContent();
		}
		return str;
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
	
}