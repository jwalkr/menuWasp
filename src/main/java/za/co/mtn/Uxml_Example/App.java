package za.co.mtn.Uxml_Example;


/** 
 *  ----------------------------------------------------------
 *  	Copyright (c) 2003 by Mobile Telephone Networks (Pty) Ltd
 *      Copyright (c) 2003 by Pharos Consulting (Pty) Ltd
 *  ----------------------------------------------------------
 *
 *  @version	1.0.01 2003-12-03
 *  @author		Pharos Consulting (Pty) Ltd; Nathan Moja, MTN (Pty) Ltd
 *	
 *  NOTE:	This is a sample WASP Client which provides 
 * 			interaction with a USSD Gateway 
 *
 *  ---------------------------------------------------------
 */

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import za.co.mtn.Uxml.UxmlConnection;
import za.co.mtn.Uxml.UxmlPdu;

public class App 
{
	static ResourceBundle bundle = ResourceBundle.getBundle("ussd");
	static int m_port			= Integer.parseInt(bundle.getString("port"));
	static String m_user		= bundle.getString("user");
	static String m_password	= bundle.getString("password");
	static String m_rsys		= bundle.getString("rsys");//rbalt_nluss4";
	static String m_node		= bundle.getString("node");
	static String m_cookie		= bundle.getString("cookie");
	static DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static Logger logger =
		Logger.getLogger( App.class );
	public static void main(String args[])
	{	
		
		//Create Map of Sessions  
		Map<String, Session> sessions = new HashMap<String, Session>();
		System.out.println("HERE");
		//Create a connection object to the USSD Access Point
		UxmlConnection conn = new UxmlConnection(); 	

		//Create a UXML PDU object
		UxmlPdu pdu = new UxmlPdu ();

		try{
			//Perform a bind to USSD
			boolean status = false;
			boolean cack = true;
			status = conn.connect(m_rsys , m_port , m_user, m_password, m_node, m_cookie, 5000 );
			logger.info("Connection status: " + status);

			//Setup control PDU
			long lastCtrl = System.currentTimeMillis();
			long delay = 30000;
			UxmlPdu cpdu = new UxmlPdu ();
			cpdu.setPdu("CTRL");
			cpdu.setReqid("0");
			cpdu.setTid("0");
			cpdu.setEncoding("ASCII");
			cpdu.setTariff("*");
			cpdu.setStatus("0");

			//Process requests from USSD
			while(status)
			{
				boolean result = conn.receive ( pdu );
				if (result == true) 
				{
					logger.debug("Received : " + pdu.getPdu() +" "+ pdu.getMsisdn() +" "+ pdu.getString() );

					if (pdu.getPdu().equals("PSSRR")){
						Session sm = new Session(conn, pdu.getMsisdn());
						sm.start();
						sm.respondMain(pdu);
						sessions.put(pdu.getMsisdn(), sm);
					}else if ((pdu.getPdu().equals("USSRC"))||(pdu.getPdu().equals("USSNC"))){
						if(!sessions.containsKey(pdu.getMsisdn())){
							Session sm = new Session(conn, pdu.getMsisdn());
							sm.start();
							sessions.put(pdu.getMsisdn(), sm);
						}
						Session sm = sessions.get(pdu.getMsisdn());
						sm.respond(pdu);
					}else if (pdu.getPdu().equals("ABORT")){
						try {
							Session sm = sessions.get(pdu.getMsisdn());
							sm.end();
							sessions.remove(pdu.getMsisdn());
						} catch (Exception e) {
							logger.error(pdu.getString());
						}
					}else if (pdu.getPdu().equals("CTRL")){
						logger.debug("Received CTRL Ack");
						cack=true;
					}
					//Reset the PDU
					pdu.clearValues();
					lastCtrl =  System.currentTimeMillis();
				}
				//Send CTRL PDU
				if ((System.currentTimeMillis() - lastCtrl)>delay){
					if (cack){
						lastCtrl =  System.currentTimeMillis();
						cpdu.setString(dfm.format(lastCtrl));
						conn.send(cpdu);
						cack=false;
					}else status=false;
				}
			}
		}catch(IOException e)
		{
			logger.error("IOException :" + e.getMessage());
		}
	}
}
