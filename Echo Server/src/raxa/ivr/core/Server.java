/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package raxa.ivr.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server extends Thread {
	
	// port that the server will listen to
	private int port;
	private ServerSocket socketListener;
	private boolean started;
	
	public Server ( int port ) {
		this.port = port;
		try {
			System.out.println("Starting server ...");
			socketListener = new ServerSocket(port);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}
	
	public void kill() {
		started = false;
	}
	
	@Override
	public void run() {
		started = true;
		int connectionId = 0;
		LinkedList<Cater> caters = new LinkedList<Server.Cater>();
		while (started) {
			try {
				System.out.println("Waiting for new connections ...");
				Socket socket = socketListener.accept();
				connectionId ++;				
				Cater cater = new Cater(socket, connectionId);
				cater.start();
				caters.add(cater);
			} catch (IOException e) {
				printError(e);
			}
			
		}
	}
	
	private void printError( Exception e) {
		System.out.println("Server encountered error :");
		System.out.println(e.getMessage());
	}
	
	
	/**
	 * For every incoming socket connection from, an instance of Cater will serve it.
	 * It's primary objective being to enable Server to efficiently handle multiple clients. 
	 */
	
	private class Cater extends Thread {	
		
		private Socket socket;
		
		// Cater id is the identity of the this cater
		private int id;
		
		public Cater (Socket socket, int id) {
			this.socket = socket;
			this.id = id;
		}
		
		@Override
		public void run(){
			try {
				System.out.println("Server received connection ...");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				String payload = reader.readLine();
				while(socket.isConnected()) {
					// TODO: Change what to do once you receive input
					System.out.println("Cater [id =" + id + "] received :" + payload);
					payload = null;
					while(payload == null) {
						payload = reader.readLine();
					}
				}
			} catch (IOException e) {				
				printError(e);
			}	
			
			
		}
		
		
		private void printError( Exception e) {
			System.out.println("Cater [id =" + id +"] encountered error :");
			e.printStackTrace();
		}
		
	}
}
