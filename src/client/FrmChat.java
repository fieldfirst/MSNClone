/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class FrmChat extends JFrame{

	private static final long serialVersionUID = 3206383279350466900L;
	private JPanel txtChat;
	private JTextField txtSend = new JTextField("Type what you want to send here.");
	private JButton btnSend = new JButton("Send");
	private JList<Object> liUsers = new JList<Object>();
	private DefaultListModel<Object> liModel = new DefaultListModel<Object>();
	private Dimension offset = new Dimension(10, 50);

	public FrmChat(){
		setTitle("Chat");
		setResizable(true);
		setSize(800, 500);
		setMinimumSize(new Dimension(800, 500));
		initComponent();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		validate();
		setVisible(true);
	}

	private void initComponent(){

		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel chatPanel = new JPanel(new BorderLayout());
		JPanel userPanel = new JPanel(new BorderLayout());

		chatPanel.add(new JLabel("Conversation :"), BorderLayout.NORTH);

		txtChat = new JPanel();
		txtChat.setLayout(new BoxLayout(txtChat, BoxLayout.PAGE_AXIS));
		txtChat.setBackground(Color.WHITE);
		JScrollPane sp = new JScrollPane(txtChat);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatPanel.add(sp, BorderLayout.CENTER);

		txtSend.setPreferredSize(new Dimension(0, 36));

		chatPanel.add(txtSend, BorderLayout.SOUTH);

		userPanel.add(new JLabel("Users :"), BorderLayout.NORTH);

		liUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		liUsers.setLayoutOrientation(JList.VERTICAL);
		liUsers.setCellRenderer(new ChatListRenderer());
		liUsers.setModel(liModel);

		JScrollPane liScroll = new JScrollPane(liUsers);
		liScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		liScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userPanel.add(liScroll, BorderLayout.CENTER);

		JPanel subButtom = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));

		subButtom.add(btnSend);
		btnSend.setEnabled(false);
		userPanel.add(subButtom, BorderLayout.SOUTH);

		mainPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(userPanel, BorderLayout.LINE_END);
		add(mainPanel);

	}

	public JTextField getTxtSend() {
		return txtSend;
	}

	public JButton getBtnSend() {
		return btnSend;
	}

	public JList<Object> getLiUsers() {
		return liUsers;
	}

	public void addInfo(String mesg) {

		Box b = Box.createHorizontalBox();
		Component space = Box.createHorizontalGlue();
		Component sep = Box.createRigidArea(offset);
		b.add(sep);
		b.add(new JLabel(mesg));
		b.add(space);
		txtChat.add(b);
		txtChat.validate();
	}

	public void addMesg(String from, Color color, String mesg) {

		Box b = Box.createHorizontalBox();
		Component space = Box.createHorizontalGlue();
		Component sep = Box.createRigidArea(offset);

		if (from.equals("self")) {
			b.add(sep);
			b.add(new Message(mesg, color, Message.LEFT));
			b.add(space);
		}
		else
		{
			b.add(space);
			b.add(new Message(mesg, color, Message.RIGHT));
			b.add(sep);
		}
		txtChat.add(b);
		txtChat.validate();
	}

	public void updateListUser(ArrayList<String> userToAdd, ArrayList<String> userToRemove) {
		for (int i=0; i<userToAdd.size(); i++)
			liModel.addElement(userToAdd.get(i));
		for (int i=0; i<userToRemove.size(); i++) {
			liModel.removeElement(userToRemove.get(i));
			addInfo("User " + userToRemove.get(i).split(":")[1] + " has leaved the conversation.");
		}
	}

	public ArrayList<String> getUserList() {
		ArrayList<String> users = new ArrayList<String>();
		for (int i=0; i<liModel.size(); i++)
			users.add((String) liModel.get(i));
		return users;
	}

	private Color stringToColor(String color) {
		switch (color) {
		case "GREEN" :
			return new Color(94, 213, 98);
		case "RED" :
			return new Color(215, 100, 100);
		case "BLUE" :
			return new Color(100, 135, 215);
		case "YELLOW" :
			return new Color(211, 215, 100);
		case "ORANGE" :
			return new Color(215, 146, 100);
		case "CYAN" :
			return new Color(100, 215, 207);
		case "PINK" :
			return new Color(215, 100, 203);
		}
		return null;
	}

	public HashMap<String, Color> getUserColorMap() {
		HashMap<String, Color> users = new HashMap<String, Color>();
		String t;
		for (int i=0; i<liModel.size(); i++) {
			t = (String) liModel.get(i);
			users.put(t.split(":")[1], stringToColor(t.split(":")[0]));
		}
		return users;
	}

}
