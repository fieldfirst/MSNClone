/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import server.UserSession;

public class MainController implements ActionListener, Observer {

	private FrmMain view;
	private Client cli;
	private HashMap<String, ChatController> chatSession = new HashMap<String, ChatController>();

	public MainController(FrmMain view){
		this.view = view;
		initController();
		cli = new Client(this);
	}

	private void initController(){
		waterTextmark();
		view.getBtnConnect().addActionListener(this);
		view.getBtnLogout().addActionListener(this);
		view.getBtnChat().addActionListener(this);
		view.getLiUsers().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (view.isUserSelected() && view.getSelectedUser().length <= 7)
					view.getBtnChat().setEnabled(true);
				else
					view.getBtnChat().setEnabled(false);
			}
		});
	}

	private void waterTextmark() {
		view.getTxtHost().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(view.getTxtHost().getText().equals(""))
					view.getTxtHost().setText("Enter host DNS/IP e.g. FiRsT-relay.no-ip.org");
			}

			@Override
			public void focusGained(FocusEvent e) {
				if(view.getTxtHost().getText().equals("Enter host DNS/IP e.g. FiRsT-relay.no-ip.org"))
					view.getTxtHost().setText("");
			}
		});
		view.getTxtUsername().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(view.getTxtUsername().getText().equals(""))
					view.getTxtUsername().setText("Your username");
			}

			@Override
			public void focusGained(FocusEvent e) {
				if(view.getTxtUsername().getText().equals("Your username"))
					view.getTxtUsername().setText("");
			}
		});
		view.getTxtPassword().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(view.getTxtPassword().getText().equals("")){
					view.getTxtPassword().setText("Your password");
					view.getTxtPassword().setEchoChar('\u0000');
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if(view.getTxtPassword().getText().equals("Your password")){
					view.getTxtPassword().setEchoChar('*');
					view.getTxtPassword().setText("");
				}
			}
		});
		view.getTxtPort().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(view.getTxtPort().getText().equals(""))
					view.getTxtPort().setText("Please enter a port number");
			}

			@Override
			public void focusGained(FocusEvent e) {
				if(view.getTxtPort().getText().equals("Please enter a port number"))
					view.getTxtPort().setText("");
			}
		});
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg1.equals("Invalid username/password")) {
			view.getLbConnectStatus().setText((String) arg1);
			view.getBtnConnect().setEnabled(true);
		}
		else if (arg1.equals("Logged in successful") || arg1.equals("Logged out successful"))
			((CardLayout) view.getMainPanel().getLayout()).next(view.getMainPanel());

		else if (arg1.equals("Connection has been lost")){
			view.getLbConnectStatus().setText("Not connected");
			((CardLayout) view.getMainPanel().getLayout()).next(view.getMainPanel());
			view.getBtnConnect().setEnabled(true);
		}

		else if(((String)arg1).split(" ")[0].equals("SSOP")) {
			String sessionID = ((String)arg1).split(" ")[1];
			if (chatSession.get(sessionID) == null) {
				ChatController c = new ChatController(new FrmChat(), view.getTxtUsername().getText(), cli);
				c.setSessionID(sessionID);
				chatSession.put(sessionID, c);
			}
		}

		else if(((String)arg1).split(" ")[0].equals("SSST")) {
			String[] st = ((String)arg1).split(" ");
			String sessionID = ((String)arg1).split(" ")[1];
			if (st.length == 3) {
				String users = ((String)arg1).split(" ")[2];
				chatSession.get(sessionID).updateUserList(users);
			}
			else
			{
				if (chatSession.get(sessionID) != null)
					chatSession.get(sessionID).forceClose();
				chatSession.remove(sessionID);
			}
		}
		else if(((String)arg1).split(" ")[0].equals("PUSH")) {
			String sessionID = ((String)arg1).split(" ")[1];
			String user = ((String)arg1).split(" ")[2];
			String mesg = ((String)arg1).split(" ", 4)[3];
			chatSession.get(sessionID).receiveMessage(user, mesg);
		}
		else {
			updateUserList(arg1);
		}
	}

	private void updateUserList(Object arg1) {
		int number = Integer.parseInt(((String)arg1).split(" ")[0]);									// Retrieve the number of online users
		String userListMesg = ((String)arg1).split(" ")[1];
		ArrayList<String> liUsers = new ArrayList<String>(Arrays.asList(userListMesg.split(",")));
		ArrayList<String> userToAdd = new ArrayList<String>();
		ArrayList<String> userToRemove = new ArrayList<String>();
		ArrayList<String> currentUser = view.getUserList();
		for (int i=0; i<number; i++) {
			if (!currentUser.contains(liUsers.get(i)) && !view.getTxtUsername().getText().equals(liUsers.get(i)))
				userToAdd.add(liUsers.get(i));
		}
		for (int i=0; i<currentUser.size(); i++) {
			if (! liUsers.contains(currentUser.get(i)))
				userToRemove.add(currentUser.get(i));
		}
		view.updateListUser(userToAdd, userToRemove);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view.getBtnConnect()){
			try {
				view.getBtnConnect().setEnabled(false);
				view.getBtnLogout().setEnabled(true);
				view.getLbConnectStatus().setText("Connecting...");
				cli.setAddress(view.getTxtHost().getText());
				cli.setPort(Integer.parseInt(view.getTxtPort().getText()));
				cli.setUsername(view.getTxtUsername().getText());
				cli.setPassword(view.getTxtPassword().getText());
				cli.startClient();

			} catch (NumberFormatException e1) {
				view.getLbConnectStatus().setText("Invalid address");
				view.getBtnConnect().setEnabled(true);
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				view.getLbConnectStatus().setText("Host is unreachable");
				view.getBtnConnect().setEnabled(true);
				e1.printStackTrace();
			} catch (IOException e1) {
				view.getLbConnectStatus().setText("Host reachable, but can't handle connection");
				view.getBtnConnect().setEnabled(true);
				e1.printStackTrace();
			}
		}

		else if (e.getSource() == view.getBtnLogout()) {
			// User must leave all session before exit
			Iterator<ChatController> u = chatSession.values().iterator();
			while(u.hasNext()) {
				ChatController temp = u.next();
				cli.leaveSession(temp.getSessionID());
				temp.forceClose();
			}
			
			cli.logout();
			view.getBtnLogout().setEnabled(false);
			view.getBtnConnect().setEnabled(true);
			view.getLbConnectStatus().setText("Not connected");
		}

		else if (e.getSource() == view.getBtnChat()) {
			String[] users = view.getSelectedUser();
			String output = new String("");

			for (int i=0; i<users.length; i++){
				if (i < users.length-1)
					output += users[i] + ",";
				else
					output += users[i] + "," + view.getTxtUsername().getText();			//Attempting to open a session (Don't forget to add yourself)
			}			
			cli.requestSession(output);
		}

	}

	public String getChatSessionID() {
		Object[] s = chatSession.keySet().toArray();
		int size = chatSession.size();
		String sessions = new String("");
		for (int i=0; i<size; i++) {
			if (i == size-1)
				sessions += s[i].toString();
			else
				sessions += s[i].toString() + ",";
		}
		return sessions;
	}
}
