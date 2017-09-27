/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dom;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author adrie
 */
public class Handler implements ErrorHandler{
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.err.println("Warning");
        throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.err.println("error");
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.err.println("fatal error");
        throw exception;
    }  
}
