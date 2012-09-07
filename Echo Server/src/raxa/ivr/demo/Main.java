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

package raxa.ivr.demo;

import java.io.IOException;

import raxa.ivr.core.Client;
import raxa.ivr.core.Server;

public class Main {
	
	public static void main(String Args[]) throws IOException {
		Server server = new Server(6789);
		server.start();
		Client client1 = new Client("localhost", 6789, 1);
		client1.start();
		Client client2 = new Client("localhost", 6789, 2);
		client2.start();
		for(int i=0; i < 100; i++){
			client1.addData(i + " ");
			client2.addData(i + " ");
		}
	}
}
