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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpClient extends Thread{
	
	private InetAddress hostAddress;
	private int hostPort;
	private int id;
	private volatile BlockingQueue<String> payload;
	
	private Socket socket;
	
	public TcpClient(InetAddress hostAddress, int hostPort,
			int clientId) throws IOException {
		this.hostAddress = hostAddress;
		this.hostPort = hostPort;
		this.id = clientId;
		payload = new LinkedBlockingQueue<String>();
		connect();
	}	
	
	public TcpClient(String hostName, int hostPort, int clientId)
			throws IOException {
		this(InetAddress.getByName(hostName), hostPort, clientId);
	}
	
	public void connect() throws IOException {
		
		int count = 0;
		while(socket == null) {
			if(count == 0) {
				System.out.println("Client attempting to connect ... ");
			}
			socket = new Socket( hostAddress, hostPort);
			count ++;
		}
	}
	
	public void sendData(byte[] data) {
		payload.add(data.toString());		
	}
	
	@Override
	public void run() {
		try {
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			while(true) {
				while(true) {
					if(!payload.isEmpty()) {
						try {
							writer.write(payload.take());
							writer.newLine();
							writer.flush();
						} catch (InterruptedException e) {
							System.out.println("Unable to send data ... ");
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to catch output stream ...");
			e.printStackTrace();
		}
		
	}

}
