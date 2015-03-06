/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.ChatSession;


public class Server extends Observable {

	private int maxNumOfThread;
	private int currentThreadNum = 1;
	private ServerSocket srvsock;
	private Controller observer;
	private ArrayList<Thread> thread_manager;
	private final Lock locker = new ReentrantLock();
	private static String userListFile = null;
	private static HashMap<String, User> userList = new HashMap<String, User>();
	private static HashMap<String, UserSession> onlineUser = new HashMap<String, UserSession>();
	private static HashMap<Integer, ChatSession> chatSession = new HashMap<Integer, ChatSession>();

	public Server(int port, int numOfThread, Controller observer)
	{
		addObserver(observer);
		if (userListFile != null && initUserList())
		{
			this.maxNumOfThread = numOfThread;
			this.observer = observer;
			try{
				srvsock = new ServerSocket(port);
				setChanged();
				notifyObservers(srvsock);
				thread_manager = new ArrayList<Thread>();
				spawnThread();

			} catch (IOException e){
				setChanged();
				notifyObservers("Port was already binded with another process");
			}
		}
		else
		{
			setChanged();
			notifyObservers("User's list file must be selected first");
		}
	}

	public void stopServer()
	{
		try {
			srvsock.close();
			for (int i=0; i < currentThreadNum-1; i++){
				thread_manager.get(i).interrupt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMaxNumOfThread() {
		return maxNumOfThread;
	}


	public static void setUserList(String userList) {
		Server.userListFile = userList;
	}

	private boolean initUserList(){
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(userListFile)));
			String line = br.readLine();
			while(line != null){
				userList.put(line.split(" ")[0], new User(line.split(" ")[0], line.split(" ")[1]));
				line = br.readLine();
			}
			br.close();
			return true;
		} catch (FileNotFoundException e){
			setChanged();
			notifyObservers("Your user's list file might be moved or changed");
			return false;
		} catch (IOException e) {
			setChanged();
			notifyObservers("Your user's list file can't be readed");
			return false;
		}
	}

	private void spawnThread(){
		if (currentThreadNum <= maxNumOfThread){
			Thread addThread = new Thread(new ServerThread(observer), "Thread no. " + currentThreadNum);
			addThread.setDaemon(false);
			addThread.start();
			thread_manager.add(addThread);
			currentThreadNum++;
		} else {
			setChanged();
			notifyObservers("Maximum thread exceeded, some client mightn't able to connect.");
		}
	}

	class ServerThread extends Observable implements Runnable {

		private UserSession currentUser = null;		// Stateful current user
		private int idlePass = 100;
		private DataInputStream in;
		private DataOutputStream out;

		// Server receive
		private static final String USER_MESG = "USER";										// username login
		private static final String PASS_MESG = "PASS";										// password
		private static final String LOGOUT_MESG = "LOGO";										// user logout
		private static final String LIST_USER_MESG = "LIST";									// get the list of current online users [user status polling]
		private static final String SEND_MESG = "SEND";										// receive an instant message from the user
		private static final String SESSION_REQ_MESG = "SSRQ";									// request a new session with the user(s)
		private static final String SESSION_POLL_MESG = "SSPO";								// session status polling
		private static final String LEAVE_SESSION = "LESS";									// User is leaving the session

		//Server send
		private static final String AUTH_FAIL_MESG = "AUTH 0";									// user authentication failed
		private static final String AUTH_SUCC_MESG = "AUTH 1";									// user authentication success
		private static final String LOGOUT_SUCC_MESG = "LOGO 1";								// user logout successful
		private static final String ONLINE_USER_MESG = "ONUS";									// send a list of current online users
		private static final String RELAY_MESG = "PUSH";										// relay an instant message to the user
		private static final String SESSION_OPEN_MESG = "SSOP";								// force the client to open a new chat session
		private static final String SESSION_STAT_MESG = "SSST";								// send the session status to the user


		//Color definitions
		private static final String GREEN = "GREEN";
		private static final String RED = "RED";
		private static final String BLUE = "BLUE";
		private static final String YELLOW = "YELLOW";
		private static final String ORANGE = "ORANGE";
		private static final String CYAN = "CYAN";
		private static final String PINK = "PINK";

		public ServerThread(Controller observer) {
			addObserver(observer);
		}

		@Override
		public void run() {

			/*

			  Protocol implementation :
			  - Stateful
			  - Centralized [client-server]

				// Server receive
					USER_MESG = "USER <username>";								// username login
					PASS_MESG = "PASS <password>";								// password
					LOGOUT_MESG = "LOGO";										// user logout
					LIST_USER_MESG = "LIST";									// get the list of current online users [user status polling]
					SEND_MESG = "SEND <chat_session_id> <message>";				// receive an instant message from the user
					SESSION_REQ_MESG = "SSRQ <users>";							// request a new session with the user(s)
					SESSION_POLL_MESG = "SSPO <chat_session_id>";				// session status polling
					LEAVE_SESSION = "LESS <chat_session_id>						// User is leaving the session


				//Server send
					AUTH_FAIL_MESG = "AUTH 0";									// user authentication failed
					AUTH_SUCC_MESG = "AUTH 1";									// user authentication success
					LOGOUT_SUCC_MESG = "LOGO 1";								// user logout successful
					ONLINE_USER_MESG = "ONUS <number> <users>";					// send a list of current online users
					RELAY_MESG = "PUSH <chat_session_id> <user> <message>";			// relay an instant message to the user
					SESSION_OPEN_MESG = "SSOP <chat_session_id>";				// force the client to open a new chat session
					SESSION_STAT_MESG = "SSST <chat_session_id> <users>";		// send the session status to the user
			 */
			Socket tcpSock = null;

			while(! Thread.currentThread().isInterrupted())
			{

				try {
					tcpSock = srvsock.accept();

					//locker.lockInterruptibly();				// Lock while still maintain interruptablity
					spawnThread();								// Spawn more thread
					tcpSock.setSoTimeout(2000); 				// Each user must sent the first incoming message within 2 seconds
					out = new DataOutputStream(tcpSock.getOutputStream());
					in = new DataInputStream(tcpSock.getInputStream());

					String raw_mesg;
					String head_mesg;

					boolean closeSocket = false;
					currentUser = new UserSession();
					//locker.unlock();

					while(! tcpSock.isClosed() && ! Thread.currentThread().isInterrupted()){							// After connection was closed then the thread itself will be ready for another TCP connection.
						//locker.lockInterruptibly();				// Lock while still maintain interruptablity

						while(in.available() == 0 && currentUser.isUserReady()) {
							Thread.sleep(200);						// wait if there is still no mesg comming in then decrease the idle pass
							if (in.available() != 0) {
								idlePass = 100;
								break;
							}
							else
								idlePass--;
							if (idlePass == 0) {						// if the idle pass is equal to zero assume that the user is gone !
								closeSocket = userLogout(tcpSock);
								break;
							}
						}
						while(in.available() != 0) {
							currentUser.addIncommingMesg(in.readUTF());
						}

						while(! currentUser.isIncommingMesgEmpty()){
							head_mesg = currentUser.peekInMesgQueue().split(" ")[0];
							raw_mesg = currentUser.pollInMesgQueue();

							if (currentUser.isUserReady()){
								if (head_mesg.equals(LOGOUT_MESG))
									closeSocket = userLogout(tcpSock);
								else if (head_mesg.equals(LIST_USER_MESG))
									listUser();
								else if (head_mesg.equals(SESSION_REQ_MESG))
									sessionRequest(raw_mesg);
								else if (head_mesg.equals(SESSION_POLL_MESG))
									chatSessionStatus(raw_mesg);
								else if (head_mesg.equals(SEND_MESG))
									relayMessage(raw_mesg);
								else if (head_mesg.equals(LEAVE_SESSION))
									userLeaveSession(raw_mesg);
							} else {
								if (head_mesg.equals(USER_MESG))
									closeSocket = checkUserExists(tcpSock, raw_mesg, closeSocket);
								else if (head_mesg.equals(PASS_MESG))
									closeSocket = checkUserPassword(tcpSock, raw_mesg, closeSocket);
								else{
									setChanged();
									notifyObservers("Unknow message from " + tcpSock.getInetAddress() + " on port " + tcpSock.getPort());
									break;
								}
							}
						}

						for(int i=1; i<=currentUser.getOutMesgQueueSize(); i++){
							out.writeUTF(currentUser.pollOutMesgQueue());
						}

						Thread.sleep(1);
						if (closeSocket) {tcpSock.close();}		// Close the socket if flag was set
						//locker.unlock();
					}

					if (currentThreadNum > 1) {Thread.currentThread().interrupt();}		//Interrupt the thread when it not in use and it is not a last thread


				} catch (IOException e) {
					setChanged();
					notifyObservers(Thread.currentThread().getName() + " has been interrupted");
					e.printStackTrace();
					Thread.currentThread().interrupt();
					currentThreadNum--;

				} catch (InterruptedException e) {
					setChanged();
					notifyObservers(Thread.currentThread().getName() + " has been interrupted");
					try {
						tcpSock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
					Thread.currentThread().interrupt();
					currentThreadNum--;
				}

			}

		}

		private void userLeaveSession(String raw_mesg) {
			int sessionID = Integer.parseInt(raw_mesg.split(" ")[1]);
			if (chatSession.get(sessionID) != null) {
				chatSession.get(sessionID).removeUser(currentUser.getUsername());	// Remove the user
				if (chatSession.get(sessionID).userCount() == 1)
					chatSession.remove(sessionID);									// Remove the chat session if chat session has only one person left
			}
		}

		private void relayMessage(String raw_mesg) {
			String unhead = raw_mesg.split(" ", 2)[1];
			String sessionID = unhead.split(" ")[0];
			String mesg = unhead.split(" ", 2)[1];
			Iterator<UserSession> users = chatSession.get(Integer.parseInt(sessionID)).getUsers().values().iterator();
			while (users.hasNext()) {
				UserSession u = users.next();
				if (! u.getUsername().equals(currentUser.getUsername())) {
					u.addOutGoingMesg(RELAY_MESG + " " + sessionID + " " + currentUser.getUsername() + " " + mesg);
				}
			}
		}

		private void chatSessionStatus(String raw_mesg) {
			String unhead = raw_mesg.split(" ")[1];
			String[] sessions = unhead.split(",");

			for (int i=0; i<sessions.length; i++) {
				ChatSession c = chatSession.get(Integer.parseInt(sessions[i]));
				if (c != null) {
					HashMap<String, UserSession> users =  c.getUsers();
					Iterator<String> color = users.keySet().iterator();
					Iterator<UserSession> u = users.values().iterator();
					String output = new String("");
					for (int k=0; k<users.size(); k++) {
						if (k == users.size()-1)
							output += color.next() + ":" + ((UserSession)u.next()).getUsername();
						else
							output += color.next() + ":" + ((UserSession)u.next()).getUsername() + ",";
					}
					currentUser.addOutGoingMesg(SESSION_STAT_MESG + " " + sessions[i] + " " + output);
				}
				else
					currentUser.addOutGoingMesg(SESSION_STAT_MESG + " " + sessions[i]);
			}
		}

		private void sessionRequest(String raw_mesg) {
			locker.lock();
			String unhead = raw_mesg.split(" ")[1];									// Unpack the head
			String[] users = unhead.split(",");										// Get the user list
			ChatSession newChatSession = new ChatSession();
			newChatSession.setSessionID(newChatSession.hashCode());
			String color = null;

			Random r = new Random();
			for (int i=0; i<users.length; i++) {
				switch (r.nextInt(7)) {
				case 0: color = GREEN; break;
				case 1: color = RED; break;
				case 2: color = BLUE; break;
				case 3: color = YELLOW; break;
				case 4: color = ORANGE; break;
				case 5: color = CYAN; break;
				case 6: color = PINK; break;
				}

				newChatSession.addUsers(color, onlineUser.get(users[i]));

			}

			chatSession.put(newChatSession.getSessionID(), newChatSession);

			Iterator<UserSession> u = newChatSession.getUsers().values().iterator();

			while (u.hasNext()) {
				UserSession temp = u.next();
				temp.addOutGoingMesg(SESSION_OPEN_MESG + " " + newChatSession.getSessionID());
				temp.addOutGoingMesg(SESSION_OPEN_MESG + " " + newChatSession.getSessionID());
			}
			locker.unlock();
		}

		private void listUser() {
			String userListMesg = "";
			Iterator<String> onlineUserIter = onlineUser.keySet().iterator();
			for(int i=0; i<onlineUser.keySet().size(); i++)
				if (i == onlineUser.keySet().size()-1)
					userListMesg += onlineUserIter.next();
				else
					userListMesg += onlineUserIter.next() + ",";
			currentUser.addOutGoingMesg(ONLINE_USER_MESG + " " + onlineUser.keySet().size() + " " + userListMesg);
		}

		private boolean userLogout(Socket tcpSock) {
			boolean closeSocket;
			closeSocket = true;
			currentUser.addOutGoingMesg(LOGOUT_SUCC_MESG);
			setChanged();
			notifyObservers("User : " + currentUser.getUsername() + " logged out successful from " + tcpSock.getInetAddress() + " on port " + tcpSock.getPort());
			onlineUser.remove(currentUser.getUsername());
			return closeSocket;
		}

		private boolean checkUserPassword(Socket tcpSock, String raw_mesg, boolean closeSocket) {
			if (currentUser.getUsername() == null || ! userList.get(currentUser.getUsername()).getPassword().equals(raw_mesg.split(" ")[1])) {
				closeSocket = true;
				currentUser.addOutGoingMesg(AUTH_FAIL_MESG);
				setChanged();
				notifyObservers("User : " + currentUser.getUsername() + " has entered an invalid password from " + tcpSock.getInetAddress() + " on port " + tcpSock.getPort());
			} else {
				currentUser.setPassword(userList.get(currentUser.getUsername()).getPassword());
				currentUser.addOutGoingMesg(AUTH_SUCC_MESG);
				onlineUser.put(currentUser.getUsername(), currentUser);
				setChanged();
				notifyObservers("User : " + currentUser.getUsername() + " logged in successful from " + tcpSock.getInetAddress() + " on port " + tcpSock.getPort());
				currentUser.setIp_address(tcpSock.getInetAddress());
				currentUser.setRemotePort(tcpSock.getPort());
			}
			return closeSocket;
		}

		private boolean checkUserExists(Socket tcpSock, String raw_mesg, boolean closeSocket) {
			if(! userList.containsKey(raw_mesg.split(" ")[1])){
				currentUser.addOutGoingMesg(AUTH_FAIL_MESG);
				closeSocket = true;
				setChanged();
				notifyObservers("Invalid username from " + tcpSock.getInetAddress() + " on port " + tcpSock.getPort());
			} else {
				currentUser.setUsername(userList.get(raw_mesg.split(" ")[1]).getUsername());
			}
			return closeSocket;
		}

	}
}