/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.util.HashMap;

import server.UserSession;

public class ChatSession {

	private int sessionID;
	private HashMap<String, UserSession> users = new HashMap<String, UserSession>();

	public ChatSession() {
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int ID) {
		this.sessionID = ID;
	}

	public void addUsers(String color, UserSession user) {
		users.put(color, user);
	}

	public void removeUser(String username) {
		Object[] key = users.keySet().toArray();
		for (int i=0; i<users.size(); i++) {
			if (users.get(key[i]).getUsername().equals(username))
				users.remove(key[i]);
		}
	}

	public HashMap<String, UserSession> getUsers() {
		return users;
	}

	public int userCount() {
		return users.size();
	}

}
