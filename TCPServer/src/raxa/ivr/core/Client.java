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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread{

	private InetAddress serverAddress;
	private int serverPort;
	private int id;
	private Socket socket;
	
	private volatile BlockingQueue<String> payload;
	private boolean started;
	
	/**
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param clientId
	 * @throws IOException 
	 */
	public Client(InetAddress serverAddress, int serverPort, int clientId) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.id = clientId;
		while(socket == null) {
			try {
				this.socket = new Socket(serverAddress, serverPort);
				System.out.println("Connection established ...");
			} catch (IOException ioe) {				
			}
		}
		payload = new LinkedBlockingQueue<String>();
		started = true;
	}
	
	
	/**
	 * 
	 * @param hostname
	 * @param serverPort
	 * @param clientId
	 * @throws UnknownHostException 
	 * @throws IOException 
	 */
	public Client(String hostname, int serverPort, int clientId) throws UnknownHostException {
		this(InetAddress.getByName(hostname), serverPort, clientId);
	}
	
	public void sendData(String data) {
		if(data != null) {			
			payload.add(data);
			// System.out.println("[Client] Added to payload ...");
		}
	}
	
	public void kill() {
		started = false;
	}
	
	public boolean isConnected() {
		if(socket == null) {
			return false;
		}
		return socket.isConnected();
	}
	
	@Override
	public void run() {
		int count = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while(started) {
				try {
					String data = payload.take();	
					if(data != null) {
						writer.write(data);
						writer.newLine();
						writer.flush();
						if(count == 1000) {
							System.out.println("TCP Client is still connected ...");
							count = 0;
						}
						count ++;
					}
				} catch (InterruptedException ie) {
					System.out.println(ie.getMessage());
				}
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}	
}