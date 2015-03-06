/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;

public class Controller implements ActionListener, Observer {
	
	private FrmMain view;
	private Server srv;
	
	public Controller(FrmMain view)
	{
		this.view = view;
		initController();
	}
	
	private void initController()
	{
		view.getBtnStart().addActionListener(this);
		view.getBtnStop().addActionListener(this);
		view.getBtnUserList().addActionListener(this);
	}
	
	public void statusPrint(String status)
	{
		view.getTxtLog().append("\n" + status);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (arg0.getSource() == view.getBtnStart())
		{
			try {
				srv = new Server(Integer.parseInt(view.getTxtPort().getText()), Integer.parseInt(view.getTxtMaxClient().getText()), this);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (arg0.getSource() == view.getBtnStop())
		{
			srv.stopServer();
			srv = null;
			System.gc();
			view.getBtnStart().setEnabled(true);
			view.getBtnStop().setEnabled(false);
			view.getTxtMaxClient().setEnabled(true);
			view.getTxtPort().setEnabled(true);
			view.getBtnUserList().setEnabled(true);
		}
		else if (arg0.getSource() == view.getBtnUserList())
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("Select a User's list file");
			
			if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION){
				view.getLbUserList().setText(fileChooser.getSelectedFile().getName());
				Server.setUserList(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		if (arg1 instanceof ServerSocket)
		{
			ServerSocket srvsock = (ServerSocket)arg1;
			statusPrint("Server is listening on " + srvsock.getInetAddress() + "[localhost] on port " + srvsock.getLocalPort());
			statusPrint("Maximum " + ((Server)arg0).getMaxNumOfThread() + " client(s) [1 thread per tcp spawn]");
			view.getBtnStart().setEnabled(false);
			view.getBtnStop().setEnabled(true);
			view.getTxtMaxClient().setEnabled(false);
			view.getTxtPort().setEnabled(false);
			view.getBtnUserList().setEnabled(false);
		}
		else
		{
			statusPrint(arg1.toString());
		}
	}
	
}