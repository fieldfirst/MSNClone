/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class UserSession {

	private String username;
	private String password;
	private InetAddress ip_address;
	private int remotePort;
	private Queue<String> inMesgQueue = new LinkedList<String>();
	private Queue<String> outMesgQueue = new LinkedList<String>();
	
	private ArrayList<String> chatSession = new ArrayList<String>();
	
	public UserSession(){
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public InetAddress getIp_address() {
		return ip_address;
	}


	public void setIp_address(InetAddress ip_address) {
		this.ip_address = ip_address;
	}


	public int getRemotePort() {
		return remotePort;
	}


	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public void addChatSession(String sessionHash) {
		chatSession.add(sessionHash);
	}
	
	public void removeChatSession(String sessionHash) {
		chatSession.remove(sessionHash);
	}
	
	public String pollInMesgQueue() {
		return inMesgQueue.poll();
	}
	
	public String pollOutMesgQueue() {
		return outMesgQueue.poll();
	}
	
	public String peekInMesgQueue() {
		return inMesgQueue.peek();
	}
	
	public String peekOutMesgQueue() {
		return outMesgQueue.peek();
	}
	
	public int getOutMesgQueueSize() {
		return outMesgQueue.size();
	}
	
	public void addOutGoingMesg(String mesg) {
		outMesgQueue.add(mesg);
	}
	
	public void addIncommingMesg(String mesg) {
		inMesgQueue.add(mesg);
	}
	
	public boolean isIncommingMesgEmpty(){
		return inMesgQueue.isEmpty();
	}
	
	public boolean isUserReady() {
		return (this.username != null && this.password != null);
	}
	
}
