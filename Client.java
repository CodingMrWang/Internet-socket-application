import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Window.Type;

public class Client extends JFrame {
	private JTextField textField;
	private JTextField textField_1;
	protected Socket cSocket;
	private JTextField textField_2;
	DataOutputStream out1;
	DataInputStream in;
	private int times=3;
	static int level=0;
	public Client() {
		repaint();
        setAlwaysOnTop(true);
        setTitle("Log In");
		setForeground(new Color(255, 250, 205));
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(495, 300);	
		textField = new JTextField();
		textField.setBounds(106, 52, 202, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		textField_1 = new JTextField();
		textField_1.setBounds(106, 108, 202, 20);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JPasswordField();
		textField_2.setBounds(106, 160, 202, 20);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);
		
		
		JLabel lblInputTheRemote = new JLabel("Input the remote IP address");
		lblInputTheRemote.setBounds(106, 27, 189, 14);
		getContentPane().add(lblInputTheRemote);
		
		JLabel lblInputTheRemote_1 = new JLabel("Input the remote Port Number");
		lblInputTheRemote_1.setBounds(106, 83, 202, 14);
		getContentPane().add(lblInputTheRemote_1);

		JLabel lblInputThePassword = new JLabel("Input the password");
		lblInputThePassword.setBounds(106, 139, 123, 14);
		getContentPane().add(lblInputThePassword);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBackground(new Color(255, 239, 213));
		btnConnect.setForeground(Color.RED);
		btnConnect.setBounds(361, 223, 91, 23);
		getContentPane().add(btnConnect);
		JLabel label = new JLabel();
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InetAddress address;
				try {
					address = InetAddress.getByName(textField.getText());
				int port=Integer.parseInt(textField_1.getText());
				int password;
				cSocket=new Socket(address,port);
		    	out1=new DataOutputStream(cSocket.getOutputStream());
		    	in = new DataInputStream(cSocket.getInputStream());
				password=Integer.parseInt(textField_2.getText());
				out1.writeInt(password);
				
				int success = in.readInt();
				if (success == 987654) {
				setVisible(false);
				String[][]File_list=SearchFile(cSocket);
				JFrame frame = new JFrame("Files Avaiable");
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.setContentPane(new gui(File_list,cSocket));
			    frame.setSize(460, 350);
			    frame.setVisible(true);
					}else{
						times--;
						repaint();
						label.setText("Wrong Password, you hava "+times+" times to try");
						label.setForeground(Color.RED);
						label.setBounds(106, 191, 300, 14);
						getContentPane().add(label);
				        if(times==0)
					System.exit(0);
					}
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		setVisible(true);
	}

	public void setSocket(Socket cSocket){
		this.cSocket = cSocket;
	}
	
	public static String[][] SearchFile(Socket cSocket) throws IOException {
		DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
		DataInputStream in = new DataInputStream(cSocket.getInputStream());
		out.writeInt(1);
		int ack1 = in.readInt();
		String[][] File_list = null;
		if (ack1 == 1){
		int File_Num = in.readInt();
		File_list=new String[File_Num][2]; 
		System.out.println("There are " + File_Num + " files in the folder");
		byte[] buffer = new byte[1024];
		for (int i = File_Num-1; i >= 0; i--) {
			int Name_Size = in.readInt();
			in.read(buffer, 0, Name_Size);
			String FileName = new String(buffer, 0, Name_Size);
			int File_Size = in.readInt();
			File_list[i][0]=FileName;
			if(File_Size>=0){
			File_list[i][1]=File_Size+" bytes";}
			else{
				File_list[i][1]="Folder";
			}
		}
		}
		return File_list;
	}

	public static void Open_Folder(Socket cSocket, String File_Path) throws IOException {
		DataOutputStream out2 = new DataOutputStream(cSocket.getOutputStream());
		 out2.writeInt(3);
		level++;
		out2.writeInt(File_Path.length());
		out2.write(File_Path.getBytes());
	}
public static void Back(Socket cSocket)throws IOException{
	DataOutputStream out3 = new DataOutputStream(cSocket.getOutputStream());
	
	out3.writeInt(4);

	level--;
}
	public static void DownloadFile(Socket cSocket,String FileName,String Address) throws IOException {
		DataOutputStream out1=new DataOutputStream(cSocket.getOutputStream());
		DataInputStream in=new DataInputStream(cSocket.getInputStream());
		out1.writeInt(2);
		int ack2 = in.readInt();
		if (ack2 == 2) {
		out1.writeInt(FileName.length());
		out1.write(FileName.getBytes());
		out1.flush();
		DataOutputStream out = new DataOutputStream(new FileOutputStream(Address + FileName));
		long size = in.readLong();
		int len;
		long counter = 0;
		System.out.println("SIZE:"+size);
		byte[] data = new byte[1024];
		while (counter < size) {
			len = in.read(data, 0, 1024);
			out.write(data, 0, len);
			counter += len;
		}
        System.out.println("File successfully download");
		out.close();
		}
		}
	 public static void DownloadAll(Socket cSocket,String Address) throws IOException{
		 DataOutputStream out1=new DataOutputStream(cSocket.getOutputStream());
			DataInputStream in=new DataInputStream(cSocket.getInputStream());
			out1.writeInt(5);
			int Number = in.readInt();
			String[] Name_list = new String[100];
			byte[]buffer=new byte[1024];
			for (int i = 0; i < Number; i++) {
				int Name_Size = in.readInt();
				in.read(buffer, 0, Name_Size);
				String FileName = new String(buffer, 0, Name_Size);
				Name_list[i] = FileName;
			}
			
			for (int i = 0; i < Number; i++) {
				DownloadFile(cSocket,Name_list[i],Address);
			}
	 }

	public static void main(String[] args) throws IOException {
		new Client();
}
}
