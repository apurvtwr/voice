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
	
	private BlockingQueue<String> payload;
	private boolean started;
	
	/**
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param clientId
	 * @throws IOException 
	 */
	public Client(InetAddress serverAddress, int serverPort, int clientId) throws IOException {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.id = clientId;
		System.out.println("Attempting to connect to server ...");
		this.socket = new Socket(serverAddress, serverPort);
		System.out.println("Connection established ...");
		payload = new LinkedBlockingQueue<String>();
		started = true;
	}
	
	
	/**
	 * 
	 * @param hostname
	 * @param serverPort
	 * @param clientId
	 * @throws IOException 
	 */
	public Client(String hostname, int serverPort, int clientId) throws IOException {
		this(InetAddress.getByName(hostname), serverPort, clientId);
	}
	
	public void addData(String data) {
		if(data != null) {
			payload.add(data);
		}
	}
	
	public void kill() {
		started = false;
	}
	
	@Override
	public void run() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while(started) {		
				String data = payload.poll();
				if(data != null) {
					writer.write(data);
					writer.newLine();
					writer.flush();
				}
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	
	
}
