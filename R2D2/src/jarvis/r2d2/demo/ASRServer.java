/*
 * Copyright 1999-2002 Carnegie Mellon University.  
 * Portions Copyright 2002 Sun Microsystems, Inc.  
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package jarvis.r2d2.demo;

import jarvis.leia.header.Header;
import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.stream.Publisher;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class ASRServer implements Observer{

	private InputStream inStream;
	private Recognizer recognizer;
	StreamDataSource sdc;
	private int RESULTS_SIZE = 20;				// keeps a check on the size of results list
	private volatile BlockingQueue<String> results;
	private Publisher pub;
	
	public ASRServer(InputStream inStream, Publisher publisher) {
		this.inStream = inStream;
		ConfigurationManager cm = new ConfigurationManager("./config/r2d2.config.xml");
        this.recognizer = (Recognizer) cm.lookup("recognizer");
        this.sdc = (StreamDataSource) cm.lookup("audioSource");
        this.pub = publisher;
        init();        
	}
	
	private void init() {
		this.results = new LinkedBlockingQueue<String>();
		recognizer.allocate();        
        sdc.setInputStream(inStream, "networkAudioStream");
	}
	
	public void setInputStream(InputStream inStream){
		this.inStream = inStream;
		try {
			recognizer.deallocate(); 
		} catch (IllegalStateException ise) {
			
		}
		init();
	}
	
	public String recognize() {
		String result = "";
		while(result.compareToIgnoreCase("") == 0) {
			System.out.println("[R2D2.ASRServer] Listening ... ");
			result += recognizer.recognize().getBestFinalResultNoFiller();
		}
		return result;
		
	}

	@Override
	public void update(Observable o, Object arg) {
		Message msg = (Message) arg;
		Header header = msg.getHeader();
		if(header.getMessageType().compareTo(MessageType.ACTION) == 0) {
			String data = msg.getData();
			StringTokenizer st = new StringTokenizer(data);
			if(st.nextToken().compareTo("R2D2") == 0) {
				// print message
				System.out.println(msg);
				String command = st.nextToken();
				System.out.println("[R2D2] Command:" + command);
				if(command.compareToIgnoreCase("RECOGNIZE") == 0) {
					String var = st.nextToken();
					String result = recognize();
					pub.sendAction("HANSOLO SET " + var + " " + result, 1, 1);
					System.out.println("Recognized: " + result);
				} 
			}
		}
		
	}
}

