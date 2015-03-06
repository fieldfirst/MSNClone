/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package server;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class FrmMain extends JFrame {

	private static final long serialVersionUID = 1488984790304475994L;
	
	private JButton btnStart = new JButton("Start");
	private JButton btnStop = new JButton("Stop");
	private JTextField txtPort = new JTextField("8888", 5);
	private JTextField txtMaxClient = new JTextField("100", 5);
	private JTextArea txtLog = new JTextArea("MyIRC multi-client immediate server 1.0a codenamed \"Eros\"", 1000, 100);
	private JButton btnUserList = new JButton("Browse");
	private JLabel lbUserList = new JLabel("not selected");
	
	public FrmMain()
	{
		setTitle("Immediate server");
		setResizable(false);
		initComponent();
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initComponent()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		JPanel panelA = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panelB = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panelD = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panelE = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		panelAInit(panelA);
		
		panelBInit(panelB);
		
		panelDInit(panelD);
		
		panelEInit(panelE);
		
		gridSetup(panel, panelA, panelB, panelD, panelE);
		
		add(panel);
		
	}

	private void gridSetup(JPanel panel, JPanel panelA, JPanel panelB,
			JPanel panelD, JPanel panelE) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 1;
		c.ipady = 1;
		c.gridx = 0;
		c.gridy = 0;
		
		panel.add(panelA, c);
		
		c.gridx = 0;
		c.gridy = 1;
		
		panel.add(panelB, c);
		
		c.gridx = 0;
		c.gridy = 2;
		
		panel.add(panelD, c);
		
		c.gridx = 0;
		c.gridy = 3;
		
		panel.add(panelE, c);
	}

	private void panelEInit(JPanel panelE) {
		txtLog.setEditable(false);
		JScrollPane sp = new JScrollPane(txtLog);
		sp.setPreferredSize(new Dimension(500, 200));
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panelE.add(sp);
	}

	private void panelDInit(JPanel panelD) {
		panelD.add(new JLabel("Server log :"));
	}

	private void panelBInit(JPanel panelB) {
		panelB.add(new JLabel("Maximum client : "));
		panelB.add(txtMaxClient);
		panelB.add(new JLabel("User's list : "));
		panelB.add(lbUserList);
		panelB.add(btnUserList);
	}

	private void panelAInit(JPanel panelA) {
		panelA.add(btnStart);
		panelA.add(btnStop);
		btnStop.setEnabled(false);
		panelA.add(new JLabel("port : "));
		panelA.add(txtPort);
	}
	
	public JButton getBtnStart() {
		return btnStart;
	}
	
	public JButton getBtnStop() {
		return btnStop;
	}

	public JTextField getTxtPort() {
		return txtPort;
	}

	public JTextField getTxtMaxClient() {
		return txtMaxClient;
	}
	
	public JTextArea getTxtLog() {
		return txtLog;
	}

	public JButton getBtnUserList() {
		return btnUserList;
	}

	public JLabel getLbUserList() {
		return lbUserList;
	}
	
}
