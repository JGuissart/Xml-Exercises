package dom;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author adrie
 */
public class Dom {
    Node rootNode;
    Document doc;
    Element newMCD;
    String filePath;
    
    public Dom(Node rn, String path) throws ParserConfigurationException 
    {
        rootNode = rn.getFirstChild(); 
        rootNode = rootNode.getNextSibling();/*getDocumentElement*/
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();/*reparti du document déjà parsé*/
        DocumentBuilder parser = factory.newDocumentBuilder();
        
        DOMImplementation DOMImpl = parser.getDOMImplementation();
        DocumentType type = DOMImpl.createDocumentType("xml", null, null);
        doc = DOMImpl.createDocument(rootNode.getNamespaceURI(), rootNode.getNodeName(), type);
        
        filePath = path;
    }
    
    public void create() throws TransformerConfigurationException, FileNotFoundException, TransformerException
    { 
       newMCD = doc.getDocumentElement();
        
       Node parcMCD = rootNode.getFirstChild();
        
       constructNode(null, newMCD, parcMCD);
        
       saveInFile();
    }
    
    
    private void constructNode(Element grandFather, Element father, Node currentNode)
    {
        Element elem = null;
        
        if(currentNode.getNodeType() == Node.TEXT_NODE)/*getNodeName().charAt(0) == '#')getNodeType*/
        {
            father.appendChild(doc.createTextNode(currentNode.getNodeValue()));
            return;
        }
        
        if(!currentNode.getNodeName().equals("attribut"))
            elem = Node(currentNode, father);

        else
        {
            String max = null;
            
            if(currentNode.getAttributes().getNamedItem("attribut") != null)
                max = currentNode.getAttributes().getNamedItem("attribut").getNodeValue();

                        
            if(max != null && max.equals("N"))
                elem = Relation(currentNode, father);
                
            else if("multi-val".equals(father.getNodeName()))
                elem = ComposedAttribute(currentNode, father, grandFather);
            else
                elem = Node(currentNode, father);
                      
        }
        
        if (currentNode.hasChildNodes())
        {
            Node firstChild = currentNode.getFirstChild(); 
            constructNode(father, elem, firstChild);
        }
        Node nextNode = currentNode.getNextSibling();
        if (nextNode != null)
        {
            constructNode(grandFather, father, nextNode); 
        }
    }
    
    private Element Node(Node nodeToAdd, Element fatherNode)
    {
        Element elem = doc.createElement(nodeToAdd.getNodeName());
        fatherNode.appendChild(elem);
            
        if(nodeToAdd.hasAttributes())
        {
            NamedNodeMap nnm = nodeToAdd.getAttributes();
                
            for(int count = 0; count < nnm.getLength(); count++)
            {
                Node tmp = nnm.item(count);
                elem.setAttribute(tmp.getNodeName(), tmp.getNodeValue());
            }
        }   
        
        return elem;
    }
    
    private Element Relation(Node nodeToAdd, Element fatherNode)
    {
        Element elem = doc.createElement("entite");
        newMCD.appendChild(elem);
                
        elem.setAttribute("nom", fatherNode.getAttribute("nom") + "_" + nodeToAdd.getAttributes().getNamedItem("nom").getNodeValue());

        Element id = doc.createElement("attribut");
        id.setAttribute("nom", nodeToAdd.getAttributes().getNamedItem("nom").getNodeValue());
        id.appendChild(doc.createElement("identifiant"));
        elem.appendChild(id);

        Element relation = doc.createElement("association");
        relation.setAttribute("nom", fatherNode.getAttribute("nom")+"_link_"+elem.getAttribute("nom"));

        Element role = doc.createElement("role");
        role.setAttribute("with", fatherNode.getAttribute("nom"));
        role.setAttribute("cardinaliteMin", "0");
        role.setAttribute("cardinaliteMax", "N");
                
        relation.appendChild(role);
                
        role = doc.createElement("role");
        role.setAttribute("with", elem.getAttribute("nom"));
        role.setAttribute("cardinaliteMin", "1");
        role.setAttribute("cardinaliteMax", "1");
                
        relation.appendChild(role);
                
        newMCD.appendChild(relation);
        
        return elem;
    }
    
    private Element ComposedAttribute(Node nodeToAdd, Element fatherNode, Element grandFatherNode)
    {
        Element elem = doc.createElement(nodeToAdd.getNodeName());
        grandFatherNode.appendChild(elem);
        elem.setAttribute("nom", fatherNode.getAttribute("nom")+"_"+nodeToAdd.getAttributes().getNamedItem("nom").getNodeValue());
                
        if(nodeToAdd.hasAttributes())
        {
            NamedNodeMap nnm = nodeToAdd.getAttributes();

            for(int count = 0; count < nnm.getLength(); count++)
            {
                Node tmp = nnm.item(count);
                elem.setAttribute(tmp.getNodeName(), tmp.getNodeValue());
            }
        }
        
        return elem;
    }
    
    private void saveInFile() throws TransformerConfigurationException, FileNotFoundException, TransformerException
    {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer idTransform = transFactory.newTransformer();
        
        idTransform.setOutputProperty(OutputKeys.METHOD, "xml");
        idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
        idTransform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        Source input = new DOMSource(doc);
        Result output = new StreamResult(new FileOutputStream(filePath));
        
        idTransform.transform(input, output);
    }
}
