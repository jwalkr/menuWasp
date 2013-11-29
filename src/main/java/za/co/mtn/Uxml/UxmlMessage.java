/*
 *  ----------------------------------------------------------
 *  @version	2.0.00 2009-01-01
 *  @author		Yusuf Kaka, MTN South Africa 
 *  Note: 	Added log4j, updated XML Parser, mavenised.
 *
 *  @version	1.0.02 2003-02-20
 *  @author		Pharos Consulting (Pty) Ltd.
 *
 *  NOTE:	This forms the basis of the uxml messages that
 *			shall send and received  over the socket.
 *          The functional messages shall inherit from this.
 *
 *  @see	UxmlLoginMessage
 *  @see	UxmlLoginRespMessage
 *  @see	UxmlPduMessage
 *  ---------------------------------------------------------
 */

package za.co.mtn.Uxml;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.*;


public abstract class UxmlMessage {
	private final static Logger logger =
		Logger.getLogger( UxmlMessage.class );
	
    // Pair:
    public final static int UXML_NAME     = 0;
    public final static int UXML_VALUE    = 1;
    public final static int UXML_MAX_PAIR = 2;

	// XML document prolog
	//public final static String  m_prolog = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	
    // Normalized data
    String [] [] m_pairs;

    // Helper strings to make life easier
    String m_root; //XML document name, eg login, ussd
	String m_child; //XML document child, eg cookie
	String m_name; //XML PDU name - used for debugging
	String m_serialout; //DOM serialized output string

    public UxmlMessage () {
        m_pairs = null;
        m_root = null;
        m_name = null;
		m_child = null;
		m_serialout = null;
	}

    abstract public int getParamCount ( );

    public void clearValues () {

        for (int i = 0; i < getParamCount(); i++ ) {
            m_pairs[i][UXML_VALUE] = "";
        }
    }

    public String [] [] getPairs ( ) {
        return  m_pairs;
    }

    public void setPairs ( String [] [] pairs ) {

		for (int i = 0; i < getParamCount(); i++ ) {
			m_pairs[i][UXML_VALUE] = pairs[i][UXML_VALUE];
		}
    }

    public String generateMessage() {

		Document doc;

        try {

			// Creat the document manaufacture
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();
	
            // Create Root Element
            Element root = doc.createElement(m_root);
 
            // Create element
            for (int i = 0; i < getParamCount(); i++) {
                root.setAttribute ( m_pairs[i][UXML_NAME], m_pairs[i][UXML_VALUE] );
            }

			// Create child element
			if (m_child != null) {
				Element item = doc.createElement(m_child);
				item.setAttribute ( m_pairs[getParamCount()][UXML_NAME], m_pairs[getParamCount()][UXML_VALUE] );
				root.appendChild( item );
			}

            // Add Root to Document
            doc.appendChild( root );

			// Serialize the output
			//m_serialout = doc.getDocumentElement().toString();
			StringWriter sw = new StringWriter();
			OutputFormat of =  new OutputFormat(doc);
			of.setEncoding("ISO-8859-1");
			XMLSerializer ser = new XMLSerializer(sw, of);
			ser.serialize(doc.getDocumentElement()); 
			m_serialout = sw.toString();

			
			// Add the XML prolog to the serialized XML doc
			//m_serialout = 	m_prolog + m_serialout;

			logger.debug(m_name + ":\n" + m_serialout);

			return m_serialout;

        } catch ( Exception ex ) {
            ex.printStackTrace();
		}

        return null;
    }

        public boolean parseMessage( String message ) throws Exception{

		Document doc;

		logger.debug("\nDebug -> PDU received: " + message );

        try {

			if(message == null || message.trim().length() <= 0)
				throw new Exception("Error, trying to parse an empty/null message.");

			// Creat the document manaufacture
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource (new CharArrayReader( message.toCharArray() )));

			// Get Root Element
			Element root = doc.getDocumentElement();

			// Get attributes
			NamedNodeMap attrbs = root.getAttributes();

			// Populate the normilized data
			for (int i = 0; i < getParamCount(); i ++ ) {
				Node value = attrbs.getNamedItem(m_pairs[i][UXML_NAME]);
				m_pairs[i][UXML_VALUE] = value.getNodeValue();
			}

        } catch (Exception ex) {
            //ex.printStackTrace();
            throw ex;
		}

        return true;
    }

}