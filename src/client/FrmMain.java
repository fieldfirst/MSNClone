/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class FrmMain extends JFrame {

	private static final long serialVersionUID = 5052238477763186718L;
	private JTextField txtHost = new JTextField("Enter host DNS/IP e.g. FiRsT-relay.no-ip.org");
	private JTextField txtPort = new JTextField("8888");
	private JTextField txtUsername = new JTextField("Your username");
	private JPasswordField txtPassword = new JPasswordField("Your password");
	private JLabel lbConnectStatus = new JLabel("Not connected");
	private JButton btnConnect = new JButton("Connect");
	private JPanel mainPanel = new JPanel(new CardLayout(0, 0));
	private JList<Object> liUsers = new JList<Object>();
	private DefaultListModel<Object> liModel = new DefaultListModel<Object>();
	private JButton btnLogout = new JButton("Logout");
	private JButton btnChat = new JButton("Chat");
	
	public FrmMain(){
		setTitle("MyIRC 1.0a codename : \"Eros\"");
		setResizable(false);
		setSize(400, 500);
		initComponent();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initComponent(){
		JPanel panelA = new JPanel(new GridBagLayout());
		JPanel panelB = new JPanel(new BorderLayout());
		
		initPanelA(panelA);
		
		initPanelB(panelB);
		
		mainPanel.add(panelA);
		mainPanel.add(panelB);
		
		add(mainPanel);
		
	}

	private void initPanelB(JPanel panelB) {
		
		JPanel headPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headPanel.add(new JLabel("Online user(s) :"));
		
		liUsers.setCellRenderer(new CustomListRenderer());
		liUsers.setModel(liModel);
		JScrollPane liScroll = new JScrollPane(liUsers);
		liScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		liScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelB.add(liScroll, BorderLayout.CENTER);
		
		JPanel buttomPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 2, 5, 10);
		buttomPanel.add(new JLabel("* Hold ctrl for selecting multiple users"), c);
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 10, 5, 5);
		buttomPanel.add(btnLogout, c);
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		btnChat.setEnabled(false);
		buttomPanel.add(btnChat, c);
		
		panelB.add(headPanel, BorderLayout.NORTH);
		panelB.add(buttomPanel, BorderLayout.SOUTH);
		
	}

	private void initPanelA(JPanel panelA) {
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5 ,5 ,5);
		c.anchor = GridBagConstraints.WEST;
		
		panelA.add(new JLabel("User Information"));
		
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 1;
		
		panelA.add(new JLabel("Host : "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		
		txtHost.setPreferredSize(new Dimension(250, 25));
		panelA.add(txtHost, c);
		
		c.gridx = 0;
		c.gridy = 2;
		
		panelA.add(new JLabel("Port : "), c);
		
		c.gridx = 1;
		c.gridy = 2;
		
		txtPort.setPreferredSize(new Dimension(250, 25));
		panelA.add(txtPort, c);
		
		c.gridx = 0;
		c.gridy = 3;
		
		panelA.add(new JLabel("Username : "), c);
		
		c.gridx = 1;
		c.gridy = 3;
		
		txtUsername.setPreferredSize(new Dimension(250, 25));
		panelA.add(txtUsername, c);
		
		c.gridx = 0;
		c.gridy = 4;
		
		panelA.add(new JLabel("Password : "), c);
		
		c.gridx = 1;
		c.gridy = 4;
		
		txtPassword.setEchoChar('\u0000');
		txtPassword.setPreferredSize(new Dimension(250, 25));
		panelA.add(txtPassword, c);
		
		c.gridx = 1;
		c.gridy = 5;
		
		c.anchor = GridBagConstraints.WEST;
		panelA.add(lbConnectStatus,c );
		
		c.gridx = 1;
		c.gridy = 6;
		
		c.anchor = GridBagConstraints.EAST;
		panelA.add(btnConnect, c);
	}

	public JTextField getTxtHost() {
		return txtHost;
	}

	public JTextField getTxtUsername() {
		return txtUsername;
	}

	public JPasswordField getTxtPassword() {
		return txtPassword;
	}

	public JLabel getLbConnectStatus() {
		return lbConnectStatus;
	}

	public JButton getBtnConnect() {
		return btnConnect;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JButton getBtnChat() {
		return btnChat;
	}

	public JTextField getTxtPort() {
		return txtPort;
	}

	public JButton getBtnLogout() {
		return btnLogout;
	}
	
	public void updateListUser(ArrayList<String> userToAdd, ArrayList<String> userToRemove) {
		for (int i=0; i<userToAdd.size(); i++)
			liModel.addElement(userToAdd.get(i));
		for (int i=0; i<userToRemove.size(); i++)
			liModel.removeElement(userToRemove.get(i));
	}
	
	public ArrayList<String> getUserList() {
		ArrayList<String> users = new ArrayList<String>();
		for (int i=0; i<liModel.size(); i++)
			users.add((String) liModel.get(i));
		return users;
	}
	
	public boolean isUserSelected() {
		return (liUsers.getSelectedIndices().length != 0);
	}
	
	public String[] getSelectedUser() {
		String[] users = new String[liUsers.getSelectedValuesList().size()];
		Iterator<Object> selectedUser = liUsers.getSelectedValuesList().iterator();
		int i = 0;
		while (selectedUser.hasNext()) {
			users[i] = selectedUser.next().toString();
			i++;
		}
		return users;
	}
	
	public JList<Object> getLiUsers() {
		return liUsers;
	}
	
}
