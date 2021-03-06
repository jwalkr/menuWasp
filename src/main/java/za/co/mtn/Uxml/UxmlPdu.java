/*
 *  ----------------------------------------------------------
 *  @version	2.0.00 2009-01-01
 *  @author		Yusuf Kaka, MTN South Africa 
 *
 *  @version	1.0.01 2003-11-29
 *  @author		Pharos Consulting (Pty) Ltd.
 *	
 *  NOTE:	The uxml pdu for the API interface. This is a 
 *			data container.
 *
 *  ---------------------------------------------------------
 */

package za.co.mtn.Uxml;


public class UxmlPdu {

    // Pair index
    final static int UPDU_PDU       = 0;
    final static int UPDU_MSISDN    = 1;
    final static int UPDU_STRING    = 2;
    final static int UPDU_TID       = 3; 
    final static int UPDU_REQID     = 4; 
    final static int UPDU_ENCODING  = 5; 
    final static int UPDU_TARIFF    = 6; 
    final static int UPDU_STATUS    = 7; 
    final static int UPDU_MAX_PRAM  = 8; 

	String [] [] m_pairs;


	/** Constructor for the class
	 * @param None
	 * @return None
	 * @exception None
	 */
    public UxmlPdu () {
        m_pairs = new String [UPDU_MAX_PRAM] [2];

        //Set the name values for the pairs    
        m_pairs[UPDU_PDU]       [0] = "PDU";
        m_pairs[UPDU_MSISDN]    [0] = "MSISDN";
        m_pairs[UPDU_STRING]    [0] = "STRING";
        m_pairs[UPDU_TID]       [0] = "TID";
        m_pairs[UPDU_REQID]     [0] = "REQID";
        m_pairs[UPDU_ENCODING]  [0] = "ENCODING";
        m_pairs[UPDU_TARIFF]    [0] = "TARIFF";
        m_pairs[UPDU_STATUS]    [0] = "STATUS";
    
        clearValues();
    }

	/** getParamCount
	 * Get the number of data (Name & Value) pairs
	 * @param None
	 * @return int - number of data pairs
	 * @exception None
	 */
    public int getParamCount ( ) {
        return  UPDU_MAX_PRAM;
    }

	/** clearValues
	 * Initialias all the value entries of the pairs
	 * @param None
	 * @return None
	 * @exception None
	 */
    public void clearValues () {

        for (int i = 0; i < getParamCount(); i++ ) {
            m_pairs[i][0] = "";
        }
    }

	/** getPairs
	 * This is for internal use. 
	 * Bulk get of the name value pair.
	 * @param None
	 * @return String [] [] - data pairs
	 * @exception None
	 */
    public String [] [] getPairs ( ) {
        return  m_pairs;
    }

	/** setPairs
	 * This is for internal use. 
	 * Bulk set of the name value pair.
	 * @param pairs - String [] [] data pairs
	 * @return None
	 * @exception None
	 */
    public void setPairs ( String [] [] pairs ) {

		for (int i = 0; i < getParamCount(); i++ ) {
			m_pairs[i][1] = pairs[i][1];
		}
    }

	/** setPdu
	 * Consult UXML spec for legal values
	 * @param pdu - string value for the PDU Type
	 * @return None
	 * @exception None
	 */
    public void setPdu ( String pdu ) {
        m_pairs[UPDU_PDU][1] = pdu;
    }

	/** getPdu
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for the PDU Type
	 * @exception None
	 */
    public String getPdu (  ) {
        return m_pairs[UPDU_PDU][1];
    }

	/** setMsisdn
	 * Consult UXML spec for legal values
	 * @param msisdn - string value for the MSISDN (digit sequnce)
	 * @return None
	 * @exception None
	 */
    public void setMsisdn ( String msisdn ) {
        m_pairs[UPDU_MSISDN][1] = msisdn;
    }

	/** getMsisdn
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for the MSISDN (digit sequnce)
	 * @exception None
	 */
    public String getMsisdn (  ) {
        return m_pairs[UPDU_MSISDN][1];
    }

	/** setString
	 * Consult UXML spec for legal values
	 * @param string - string value for the string (digit sequnce|message) 
	 * @return None
	 * @exception None
	 */
    public void setString ( String string ) {
        m_pairs[UPDU_STRING][1] = string;
    }

	/** getString
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for the string (digit sequnce|message) 
	 * @exception None
	 */
    public String getString (  ) {
        return m_pairs[UPDU_STRING][1];
    }

	/** setTid
	 * Consult UXML spec for legal values
	 * @param tid - string value for the transaction id
	 * @return None
	 * @exception None
	 */
    public void setTid ( String tid ) {
        m_pairs[UPDU_TID][1] = tid;
    }

	/** getTid
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for the transaction id
	 * @exception None
	 */
    public String getTid (  ) {
        return m_pairs[UPDU_TID][1];
    }

	/** setReqid
	 * Consult UXML spec for legal values
	 * @param req - string value for the request id
	 * @return None
	 * @exception None
	 */
    public void setReqid ( String reqid ) {
        m_pairs[UPDU_REQID][1] = reqid;
    }

	/** getReqid
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for the request id
	 * @exception None
	 */
    public String getReqid (  ) {
        return m_pairs[UPDU_REQID][1];
    }

	/** setEncoding
	 * Consult UXML spec for legal values
	 * @param encoding - string value for type of encoding for the message string
	 * @return None
	 * @exception None
	 */
    public void setEncoding ( String encoding ) {
        m_pairs[UPDU_ENCODING][1] = encoding;
    }

	/** getEncoding
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for type of encoding for the message string
	 * @exception None
	 */
    public String getEncoding (  ) {
        return m_pairs[UPDU_ENCODING][1];
    }

	/** setTariff
	 * Consult UXML spec for legal values
	 * @param tariff - string value for tariff
	 * @return None
	 * @exception None
	 */
    public void setTariff ( String tariff ) {
        m_pairs[UPDU_TARIFF][1] = tariff;
    }

	/** getTariff
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for tariff
	 * @exception None
	 */
    public String getTariff (  ) {
        return m_pairs[UPDU_TARIFF][1];
    }

	/** setStatus
	 * Consult UXML spec for legal values
	 * @param status - string value for status
	 * @return None
	 * @exception None
	 */
    public void setStatus ( String status ) {
        m_pairs[UPDU_STATUS][1] = status;
    }

	/** getStatus
	 * Consult UXML spec for legal values
	 * @param None
	 * @return String - string value for status
	 * @exception None
	 */
    public String getStatus (  ) {
        return m_pairs[UPDU_STATUS][1];
    }

}
