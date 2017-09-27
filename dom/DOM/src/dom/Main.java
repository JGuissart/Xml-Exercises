/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dom;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author adrie
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParserConfigurationException{
        DocumentBuilderFactory facto = DocumentBuilderFactory.newInstance();
        
        facto.setValidating(true);
        facto.setNamespaceAware(true);
        facto.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parse = facto.newDocumentBuilder();
        
        parse.setErrorHandler(new Handler());
        
        try 
        {
            Document doc = parse.parse(args[0]);
            Node rootNode  = doc;
            String filePath = args[0].substring(0, args[0].length() - 4) + "_mod.xml";

            Dom creator = new Dom(rootNode, filePath);
            creator.create();
        }   
        catch (SAXException ex) 
        {
            System.err.println("Erreur sax : " + ex);
        } 
        catch (IOException ex) 
        {
            System.err.println("Erreur IO " + ex);
        } 
        catch (TransformerConfigurationException ex) 
        {
            System.err.println("Erreur transform : " + ex);
        } 
        catch (TransformerException ex) 
        {
            System.err.println("Erreur transformation xml : " + ex);
        }
        
    }
    
}
