package za.co.mtn.Uxml_Example;


import org.apache.log4j.Logger;

import za.co.mtn.Uxml.UxmlConnection;
import za.co.mtn.Uxml.UxmlPdu;

public class Session extends Thread{
	private final static Logger logger =
			Logger.getLogger(Session.class);
	private boolean exit = false;
	UxmlConnection connection;
	String mainMenu;
	String msisdn;
	int timer = 0;
	private int menulevel = 0;
	private int fromitem = 0;
	public Session(UxmlConnection connect, String msisdn){
		connection = connect;
		this.msisdn =  msisdn;
		mainMenu = "Test Menu" +
				"1) Option 1\n" +
				"2) Option 2\n" +
				"0) Quit\n" +
				"?";

		logger.info(msisdn);
	}
	public void respondMain(UxmlPdu pdu){
		pdu.setPdu("USSRR");
		pdu.setString(mainMenu);
		connection.send(pdu);
	}
	public void respond(UxmlPdu pdu){
		timer = 0;
		pdu.setPdu("USSRR");
		if (menulevel == 0){
			if (pdu.getString().startsWith("1")){
				pdu.setString("Submenu 1" +
						"1) Option 1.1");
				fromitem = 1;
				menulevel = 1;
			}else if(pdu.getString().startsWith("2")){
				pdu.setString("Item 2");
				fromitem = 2;
				menulevel = 0;
			}else {
				pdu.setPdu("PSSRC");
				pdu.setString("Goodbye!");
				exit = true;
			}
		}else if (menulevel == 1){
			if (fromitem == 1){
				pdu.setString("Item 1.1");
				menulevel = 0;
			}
		}
		connection.send(pdu);
	}
	public void push(String number){
		UxmlPdu ppdu = new UxmlPdu ();
		ppdu.setPdu("USSRR");
		ppdu.setMsisdn(number);
		ppdu.setReqid("50");
		ppdu.setTid("0");
		ppdu.setEncoding("ASCII");
		ppdu.setTariff("*");
		ppdu.setStatus("0");
		ppdu.setString("Poooosh");
		connection.send(ppdu);
	}
	public void end(){
		exit = true;
	}

	public void run() {
		while (!exit) {
			synchronized(this) {
				try {
					wait(1000);
					timer +=1;
					if (timer > 300) exit = true;
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}
		end();
	}
}