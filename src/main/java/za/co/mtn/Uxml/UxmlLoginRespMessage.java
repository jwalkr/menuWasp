/* 
 *  ----------------------------------------------------------
 *  @version	2.0.00 2009-01-01
 *  @author		Yusuf Kaka, MTN South Africa 
 *
 *  @version	1.0.01 2003-11-29
 *  @author		Pharos Consulting (Pty) Ltd.
 *
 *  NOTE:	The uxml login response that will be received
 *          and send
 *
 *  @see	UxmlMessage
 *  ---------------------------------------------------------
 */

package za.co.mtn.Uxml;


public class UxmlLoginRespMessage extends UxmlMessage {

    // Pair index
    final static int ULOGINRESP_COOKIE      = 0; 
    final static int ULOGINRESP_MAX_PRAM    = 1; 

    public UxmlLoginRespMessage () 
    {
        m_pairs = new String [ULOGINRESP_MAX_PRAM] [UXML_MAX_PAIR];

        //Set the name values for the pairs    
        m_pairs[ULOGINRESP_COOKIE]  [UXML_NAME] = "VALUE";

        clearValues();

        // Set abstract class helper values
        m_root = "cookie";
        m_name = "UxmlLoginRespMessage";

    }

    public int getParamCount ( ) {
        return  ULOGINRESP_MAX_PRAM;
    }

    public void setCookie ( String cookie ) {
        m_pairs[ULOGINRESP_COOKIE][UXML_VALUE] = cookie;
    }

    public String getCookie (  ) {
        return m_pairs[ULOGINRESP_COOKIE][UXML_VALUE];
    }
}
