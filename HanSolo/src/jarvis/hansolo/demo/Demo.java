/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.hansolo.demo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import org.json.JSONException;

import jarvis.hansolo.graph.Task;
import jarvis.hansolo.graph.TaskNode;
import jarvis.hansolo.graph.TaskType;
import jarvis.hansolo.utils.tools.Variable;
import jarvis.hansolo.utils.tools.VariableType;
import jarvis.leia.header.Header;
import jarvis.leia.message.Message;
import jarvis.leia.message.MessageType;
import jarvis.leia.stream.MessageHandler;
import jarvis.leia.stream.Publisher;
import jarvis.leia.stream.Subscriber;
import jarvis.mrsinterface.MRSInterface;

public class Demo implements Observer{
	
	private MessageHandler msgHandle;
	private volatile boolean CONTINUE_DIALOG; // is a variable to reset the dialog
	private volatile boolean NEXT;		// ensures that the dialog does not proceed to the next step until asked to do so
	private HashMap<String, String> variables;
	
	public Demo() throws UnknownHostException, IOException {
		this.msgHandle = new 
				MessageHandler("HANSOLO", 11, "luckyluke.pc.cs.cmu.edu", 1089, 1090);
		this.msgHandle.getSubscriber().addObserver(this);
		this.CONTINUE_DIALOG = false;
		this.NEXT = false;
		this.variables = new HashMap<String, String>();
	}
	
	private MessageHandler getMessageHandler() {
		return this.msgHandle;
	}
	
	private boolean getContinueDialog() {
		return this.CONTINUE_DIALOG;
	}
	
	private boolean getNext() {
		return this.NEXT;
	}
	
	/**
	 * Resets value of next
	 * @param next
	 */
	private void setNext(boolean next) {
		this.NEXT = next;
	}
	
	public static void main(String Args[]) throws Exception {
		
		Demo server = new Demo();
		MessageHandler msgHandle = server.getMessageHandler();
		while(true) {
			//System.out.println("CONTINUE Dialog: " + server.getContinueDialog());
			if(server.getContinueDialog()) {
				server.CONTINUE_DIALOG = false;
				routine(msgHandle, server);
			}
		}
		 
	}
	
	public static void routine(MessageHandler msgHandle, Demo server) 
			throws UnknownHostException, IOException, JSONException {
		msgHandle.getPublisher().sendAction("C3PO_SPEAK Welcome to Raxa Medicine Reminder service", 1, 1);
		msgHandle.getPublisher().sendAction("C3PO_SPEAK Please tell me your name", 1, 1);
		msgHandle.getPublisher().sendAction("R2D2 RECOGNIZE NAME", 1, 1);
		String name = server.variables.remove("NAME");
		while(name == null) {
			name = server.variables.remove("NAME");
		}
		
		MRSInterface mrsInterface = new MRSInterface();
		LinkedList<String> prompts = mrsInterface.getPrescription(name);
		
		Iterator<String> iter = prompts.iterator();
		while(iter.hasNext()) {
			msgHandle.getPublisher().sendAction("C3PO_SPEAK " + iter.next() , 1, 1);
		}
		msgHandle.getPublisher().sendAction("C3PO_SPEAK Thank you. have a nice day", 1, 1);
		
	}
	
	

	@Override
	public void update(Observable o, Object arg) {
		Message msg = (Message) arg;
		Header header = msg.getHeader();
		if(header.getMessageType().compareTo(MessageType.ACTION) == 0) {
			String data = msg.getData();
			StringTokenizer st = new StringTokenizer(data);
			if(st.nextToken().compareTo("HANSOLO") == 0) {
				// print message
				System.out.println(msg);
				String command = st.nextToken();
				System.out.println("[HanSolo] Command:" + command);
				if(command.compareToIgnoreCase("STOP") == 0) {
					CONTINUE_DIALOG = false;
				} else if(command.compareToIgnoreCase("START") == 0) {
					CONTINUE_DIALOG = true;
				} else if(command.compareTo("NEXT") == 0) {
					NEXT = true;
				} else if(command.compareTo("SET") == 0) {
					
					String var = st.nextToken();
					System.out.println("var: " + var);
					String value = st.nextToken();
					System.out.println("Value: " + value);
					variables.put(var, value);
					System.out.println("Var: " + var + "  value :" + value);
				}
			}
		}
	}
}
