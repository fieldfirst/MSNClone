/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatController {

	private FrmChat view;
	private String sessionID;
	private String username;
	private Client cli;

	public ChatController(FrmChat view, String username, Client cli){
		this.view = view;
		this.username = username;
		this.cli = cli;
		initController();
	}

	public void setSessionID(String id) {
		this.sessionID = id;
	}

	public String getSessionID() {
		return this.sessionID;
	}

	private void initController(){
		view.getTxtSend().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if (view.getTxtSend().getText().equals(""))
					view.getTxtSend().setText("Type what you want to send here.");
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				if (view.getTxtSend().getText().equals("Type what you want to send here."));
				view.getTxtSend().setText("");
			}
		});

		view.getTxtSend().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !view.getTxtSend().getText().equals("")) {
					loopbackMessage(view.getTxtSend().getText());
					cli.sendMessage(sessionID + " " + view.getTxtSend().getText());
					view.getTxtSend().setText("");
					view.getBtnSend().setEnabled(false);
					view.getLiUsers().requestFocus();		// Force focusing
				}
				else {
					if (! view.getTxtSend().getText().equals(""))
						view.getBtnSend().setEnabled(true);
					else
						view.getBtnSend().setEnabled(false);
				}
			}
		});

		view.getBtnSend().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loopbackMessage(view.getTxtSend().getText());
				cli.sendMessage(sessionID + " " + view.getTxtSend().getText());
				view.getTxtSend().setText("");
				view.getBtnSend().setEnabled(false);
				view.getLiUsers().requestFocus();		// Force focusing
			}
		});

		view.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				cli.leaveSession(sessionID);
			}
		});

	}

	public void updateUserList(String users) {
		ArrayList<String> liUsers = new ArrayList<String>(Arrays.asList(users.split(",")));
		ArrayList<String> userToAdd = new ArrayList<String>();
		ArrayList<String> userToRemove = new ArrayList<String>();
		ArrayList<String> currentUser = view.getUserList();
		for (int i=0; i<liUsers.size(); i++) {
			if (! currentUser.contains(liUsers.get(i)))
				userToAdd.add(liUsers.get(i));
		}
		for (int i=0; i<currentUser.size(); i++) {
			if (! liUsers.contains(currentUser.get(i)))
				userToRemove.add(currentUser.get(i));
		}
		view.updateListUser(userToAdd, userToRemove);
	}

	public void receiveMessage(String user, String mesg) {
		view.addMesg("other", view.getUserColorMap().get(user), mesg);
	}

	private void loopbackMessage(String mesg) {
		view.addMesg("self", view.getUserColorMap().get(username), mesg);
	}
	
	public void forceClose() {
		view.dispose();
	}

}
