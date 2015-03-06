/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;



public class Client extends Observable{

	private Queue<String> outMesgQueue = new LinkedList<String>();
	private Queue<String> inMesgQueue = new LinkedList<String>();
	private ReentrantLock locker = new ReentrantLock();
	private Socket tcpSock;
	private Thread ct;
	private ClientThread cli;
	private DataInputStream in;
	private DataOutputStream out;
	private MainController observer;
	private String address;
	private int port;

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

	public Client(MainController observer){
		this.observer = observer;
		addObserver(observer);
	}

	public void setUsername(String username){
		outMesgQueue.add(USER_MESG + " " + username);
	}

	public void setPassword(String password){
		outMesgQueue.add(PASS_MESG + " " + password);
	}

	public void setAddress(String address){
		this.address = address;
	}

	public void requestSession(String users) {
		outMesgQueue.add(SESSION_REQ_MESG + " " + users);
	}

	public void sendMessage(String mesg) {
		outMesgQueue.add(SEND_MESG + " " + mesg);
	}

	public void leaveSession(String sessionID) {
		outMesgQueue.add(LEAVE_SESSION + " " + sessionID);
	}

	public void setPort(int port){
		this.port = port;
	}

	public void startClient() throws UnknownHostException, IOException{
		tcpSock = new Socket(address, port);
		in = new DataInputStream(tcpSock.getInputStream());
		out = new DataOutputStream(tcpSock.getOutputStream());
		cli = new ClientThread(observer);
		ct = new Thread(cli);
		ct.start();
	}

	public void logout(){
		outMesgQueue.add(LOGOUT_MESG);
		outMesgQueue.add(LOGOUT_MESG);				// Try a second attempt if the first one failed
	}

	private class ClientThread extends Observable implements Runnable {

		public ClientThread(MainController observer){
			addObserver(observer);
		}

		private void queueReset(){
			inMesgQueue.clear();
			outMesgQueue.clear();
		}

		@Override
		public void run() {

			try {

				boolean isLoggedin = false;
				boolean closeSocket = false;
				String raw_mesg;
				String head_mesg;
				int LIST_POLL_DELAY = 0;

				while (! tcpSock.isClosed()){
					locker.lock();
					while (in.available() != 0){
						inMesgQueue.add(in.readUTF());
					}
					while (! inMesgQueue.isEmpty()){

						head_mesg = inMesgQueue.peek().split(" ")[0];
						raw_mesg = inMesgQueue.poll();

						if (raw_mesg.equals(AUTH_FAIL_MESG)){
							setChanged();
							notifyObservers("Invalid username/password");
							closeSocket = true;
						} else if (raw_mesg.equals(AUTH_SUCC_MESG)){
							setChanged();
							notifyObservers("Logged in successful");
							isLoggedin = true;
						} else if (raw_mesg.equals(LOGOUT_SUCC_MESG)){
							setChanged();
							notifyObservers("Logged out successful");
							isLoggedin = false;
							closeSocket = true;
							queueReset();
						} else if (head_mesg.equals(ONLINE_USER_MESG)){
							setChanged();
							notifyObservers(raw_mesg.split(" ", 2)[1]);			// Only 2 string segments allowed [unpack the ONUS head]
						} else if (head_mesg.equals(SESSION_OPEN_MESG)){
							setChanged();
							notifyObservers(raw_mesg);
						} else if (head_mesg.equals(SESSION_STAT_MESG)) {
							setChanged();
							notifyObservers(raw_mesg);
						} else if (head_mesg.equals(RELAY_MESG)) {
							setChanged();
							notifyObservers(raw_mesg);
						} else {
							closeSocket = true;									// Close socket when invalid message was received
						}

					}
					if (isLoggedin){
						if (LIST_POLL_DELAY == 0){
							outMesgQueue.add(LIST_USER_MESG);						// If user has logged in then keep polling !!!
							if (! observer.getChatSessionID().equals(""))
								outMesgQueue.add(SESSION_POLL_MESG + " " + observer.getChatSessionID());
							LIST_POLL_DELAY = 60;
						}
						else
							LIST_POLL_DELAY--;
					}

					for(int i=0; i<outMesgQueue.size(); i++){
						out.writeUTF(outMesgQueue.poll());
					}

					Thread.sleep(100);
					if (closeSocket) {tcpSock.close();}

					locker.unlock();
				}

			} catch (IOException e) {
				setChanged();
				notifyObservers("Connection has been lost");
				e.printStackTrace();
			} catch (InterruptedException e) {
				setChanged();
				notifyObservers("Connection has been lost");
				e.printStackTrace();
			}

		}

	}

}
