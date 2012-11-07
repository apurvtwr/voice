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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Demo {
	
	public static void main(String args[]) throws IOException {
		MessageHandler messageHandler = new MessageHandler("C3PO", 3, "localhost", 1089, 1090);
		Publisher publisher = messageHandler.getPublisher();
		
		ServerSocket controlServer = new ServerSocket(9990);
		ServerSocket dataServer = new ServerSocket(9991);
		while(true) {
			Socket controlSocket = controlServer.accept();
			Socket dataSocket = dataServer.accept();
			ASRServer asrServer = new ASRServer(dataSocket.getInputStream());
			asrServer.start();
			System.out.println("[C3PO] Start ..");
			while(!dataSocket.isClosed() && !controlSocket.isClosed()) {
				String result = asrServer.recognize();
				if(result != null) {
					System.out.println("C3PO_RESULT: " + result);
					publisher.sendInfo("C3PO_RESULT " + result, 1, 1);
				}
			}
			
			
		}
	}

}
