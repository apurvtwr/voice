/*
 *
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package jarvis.r2d2.demo;



import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Observable;
import java.util.Observer;


public class Demo {
	
	public static void main(String args[]) throws Exception {
		
		MessageHandler messageHandler = new MessageHandler("R2D2", 3, "luckyluke.pc.cs.cmu.edu", 1089, 1090);
		Publisher publisher = messageHandler.getPublisher();
		
		ServerSocket controlServer = new ServerSocket(9990);
		ServerSocket dataServer = new ServerSocket(9991);
		
		Socket controlSocket = controlServer.accept();
		Socket dataSocket = dataServer.accept();
		ASRServer asrServer = new ASRServer(dataSocket.getInputStream(), publisher);
		messageHandler.getSubscriber().addObserver(asrServer);
		
		// all that remains is to wait ....
		while(true) {}
	}

}

