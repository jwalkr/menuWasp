/* 
 *  ----------------------------------------------------------
 *  @version	2.0.00 2009-01-01
 *  @author		Yusuf Kaka, MTN South Africa 
 *  
 *  NOTE: Added Maven support, updated supporting libraries
 * 
 *  @version	1.0.04 2004-01-13
 *  @author		Pharos Consulting (Pty) Ltd.
 *	
 *  NOTE:	Cut and past error on the send method.
 *
 *  @version	1.0.03 2004-01-13
 *  @author		Pharos Consulting (Pty) Ltd.
 *	
 *  NOTE:	Removed PrintWriter and replaced it with a normal OutputStream.
 *			Forced the InputStreamReader to ISO-8859-1.
 *          Added version method, obj.version(), shall return the version string
 *
 *  @version	1.0.02 2004-01-08
 *  @author		Pharos Consulting (Pty) Ltd.
 *	
 *  NOTE:	Change the connect only true when cookie received.
 *			Increase the connect receive time from 5 seconds to 20 seconds.
 *          Added new function that allowes user to spec own time out 
 *
 *  @version	1.0.01 2003-12-01
 *  @author		Pharos Consulting (Pty) Ltd.
 *	
 *  NOTE:	This is the connection object whic provides the 
 *			API for the UXML connection.
 *
 *			connection:
 *			-rsys nlap1@nluss2 -port 8001 -u TEST_902 -p test_902 -cnode 902 -cookie nlap1
 *  ---------------------------------------------------------
 */

package za.co.mtn.Uxml;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;


public class UxmlConnection {

    final static long TIME_OUT_GRAIN   = 100;   // 100 miliseconds

	final static long MAX_TIME_OUT	   = 20000; // 20 seconds

	final static long MIN_COOKIE_LENGTH	= 6; // 6 characters

	final static String VERSION	= "1.1.0"; // 6 characters

	// Data transver
    Socket m_socket		= null; 
	OutputStream m_out  = null;
	InputStreamReader m_in = null;

	// Messages handled buy the connection
	UxmlLoginMessage		m_login;
	UxmlLoginRespMessage	m_login_resp;
	UxmlPduMessage			m_req;
	UxmlPduMessage			m_resp;

	//
	boolean m_connected = false;

	//
	String m_buffer = "";

	// Push 
	char m_push;
	
	private final static Logger logger =
		Logger.getLogger( UxmlConnection.class );

	/** UxmlConnection
	 * Constructor for the class
	 * @param None
	 * @return None
	 * @exception None
	 */
	public UxmlConnection () {

		m_login			= new UxmlLoginMessage ();
		m_login_resp	= new UxmlLoginRespMessage ();
		m_req			= new UxmlPduMessage ();
		m_resp			= new UxmlPduMessage ();

		m_push = (char)255;
	}
	
	/** connect
	 * Connect to the Uxml access point with max default timeout of 20 seconds
	 * @param remote_system - access point @ host,  eg "nlap1@nluss2"
	 * @param port - port of the access point,  eg "8001"
	 * @param user - user that are allowe to access the access point,  eg "TEST_902"
	 * @param password - password of the user, eg "test_902"
	 * @param node_id - my id i want the access node to know me as, eg "902"
	 * @param cookie - security string, eg "nlap1"
	 * @return boolean - boolean value which means: success(true), failure(false)
	 * @exception IOException
	 */
	public boolean connect ( String remote_system, int port, String user, String password, String node_id, String cookie ) throws IOException {

		boolean result = false;

		try
		{
			// Connect using the defualt connection time out		
			result = connect ( remote_system, port, user, password, node_id, cookie, MAX_TIME_OUT );

		} catch ( IOException ex ) {
			throw ex;
		}
		 
		return result;
	}

	/** connect
	 * Connect to the Uxml access point with user timeout
	 * @param remote_system - access point @ host,  eg "nlap1@nluss2"
	 * @param port - port of the access point,  eg "8001"
	 * @param user - user that are allowe to access the access point,  eg "TEST_902"
	 * @param password - password of the user, eg "test_902"
	 * @param node_id - my id i want the access node to know me as, eg "902"
	 * @param cookie - security string, eg "nlap1"
	 * @param msec - tiem to wait for response 
	 * @return boolean - boolean value which means: success(true), failure(false)
	 * @exception IOException
	 */
	public boolean connect ( String remote_system, int port, String user, String password, String node_id, String cookie, long msec ) throws IOException {

		boolean result = false;

		// Populate the login message
		m_login.setUser ( user );
		m_login.setPassword ( password );
		m_login.setRmt_Sys ( remote_system );  
		m_login.setNode_Id ( node_id );
		m_login.setCookie ( cookie );

		//"ussdgw@192.168.1.1"
		int loc = remote_system.indexOf('@');
		if( loc > 0 ) {

			try
			{
				// Kill the socket
				if (isConnected() == true) {
					disconnect();
				}

				// Creat socket and connect to server
				m_socket = new Socket(remote_system.substring(loc + 1), port);

				if (m_socket != null) {
					// Create the reader and writer for the socket
					m_out = m_socket.getOutputStream();
					m_in =	new InputStreamReader(	m_socket.getInputStream(), "ISO-8859-1" );

					// Send the login	
					String temp = m_login.generateMessage ();

					m_out.write( 0 );
					m_out.write( temp.getBytes(), 0,  temp.length() );
					m_out.write( m_push );
					m_out.write( m_push );
					m_out.flush(  );

					// Wait for the response		
					result = receive ( m_login_resp, msec );

					// Evaluate the login response 	
					//   must at least have a min length of 6 characters
					if ( m_login_resp.getCookie().length() < MIN_COOKIE_LENGTH) { 
						result = false;
					}
					
					// Update the connection status shall be reported by is connected
					m_connected = result;
				}
				// Kill the socket
				if (result == false) {
					disconnect();
				}

			} catch ( IOException ex ) {
				logger.error(ex.toString());
				throw ex;
			}
			
		}
		 
		return result;
	}

	/** send
	 * @param pdu - data to send 
	 * @return boolean - boolean value which means: success(true), failure(false)
	 * @exception None
	 */
	public boolean send ( UxmlPdu pdu ) {

		boolean result = true;

		// Use the new cookie recived from the remote system
		m_resp.setPairs(pdu.getPairs());
		m_resp.setCookie(m_login_resp.getCookie());
		
		// Make sure the output pipe is valid
		if (m_out != null) {

			// Send the login	
			String temp = m_resp.generateMessage ();
			try
			{
				m_out.write( 0 );
				m_out.write( temp.getBytes(), 0,  temp.length() );
				m_out.write( m_push );
				m_out.write( m_push );
				m_out.flush(  );

			} catch ( IOException ex ) {
				logger.error(ex.toString());
			}


		} else {
			result = false;
		}

		return result;
	}

	/** receive
	 * @param pdu - pdu to take the received data
	 * @return boolean - boolean value which means: success(true), failure(false)
	 * @exception IOException
	 */
	public boolean receive ( UxmlPdu pdu ) throws IOException {

		boolean result = false;

		try
		{
			result = receive ( pdu, MAX_TIME_OUT );
		} catch ( IOException ex ) {
			logger.debug("\nPDU Received: " + ex.toString());
			throw ex;
		}

		return result;
	}

	/** receive
	 * @param pdu - pdu to take the received data
	 * @param msec - millisecond time out (how long to wait for?)
	 * @return boolean - boolean value which means: success(true), failure(false)
	 * @exception IOException
	 */
	public boolean receive ( UxmlPdu pdu, long msec ) throws IOException {

		boolean result = false;

		try
		{
			result = receive ( m_req, msec );
		} catch ( IOException ex ) {
			logger.debug(ex.toString());
			throw ex;
		}

		if ( result == true ) {
			pdu.setPairs(m_req.getPairs());
		}

		return result;
	}

	/*private boolean receive ( UxmlMessage pdu )  throws IOException  {

		boolean result = false;

		try
		{
			result = receive( pdu, MAX_TIME_OUT );
		} catch ( IOException ex ) {
			if ( m_debug == true ) {
				logger.debug(ex.toString());
			}
			throw ex;
		}

		return result;
	}*/

	private boolean receive ( UxmlMessage pdu, long msec )  throws IOException {

		boolean result = false;
		long byte_count = 0;
		long try_count = 0;
		long max_count = msec/TIME_OUT_GRAIN;
		m_buffer = "";

		// Make sure the input pipe is valid
		if ( m_in != null) {
			try	{		

				// Wait for the response		
				while (m_socket.isConnected() == true) {
					   
					// Check if data availible?
					if (m_in.ready() == true) {

						// Yes - then read it
						int temp = m_in.read();
						
						// If the terminating char is the very first char then ignore
						// until valid characters received
						if ((byte_count > 0)&&(temp == 255)) {
							try {
								pdu.parseMessage(m_buffer);
							} 
							catch (Exception e) {
								logger.error(e);
							}
							m_buffer = "";
							result = true;
							break;
						}

						// Do not add the terminating char to the buffer
						if (temp != 255) {
							m_buffer += (char)temp;
							byte_count ++;
						}

					} else {

						if (try_count < max_count )	{
							// Wait a bit for data to arrive
							try {
								Thread.sleep(TIME_OUT_GRAIN);
							} catch ( InterruptedException ex ) {
								logger.error(ex.toString());
								break;
							}
							try_count ++;
						} else {	
							// Time out occurred force the result to false
							result = false;
							break;
						}
					}
				}

			} catch ( IOException ex ) {
				logger.error(ex.toString());
				throw ex;
			}
		}

		return result;
	}

	/** isConnected
	 * @param None
	 * @return boolean -  connected(true), not connected(false)
	 * @exception None
	 */
	public boolean isConnected () {

		boolean result = false;

		// Check the connection state first
		// then the socket
		if ((m_connected == true )&&(m_socket != null)) {
			result = m_socket.isConnected();
		} 

		return result;
	}

	/** disconnect
	 * @param None
	 * @return None
	 * @exception IOException
	 */
    public void disconnect() throws IOException {
		if (m_socket != null) {
			if (m_socket.isConnected() == true) {
				m_out.flush();
				m_out.close();
				m_socket.close();
			}
		}
		
		// Make they are cleen for new start
		m_socket = null;
		m_out = null;
		m_in = null;
		m_connected = false;		
    }

	/** version
	 * @param None
	 * @return String verion number
	 * @exception None
	 */
    public String version() {
		return VERSION; 
    }

}

