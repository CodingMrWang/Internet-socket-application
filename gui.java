import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

public class gui extends JPanel {

  JList list;
  DefaultListModel model;
  private final JButton btnNewButton = new JButton("Download All Files");
  private final JButton btnNewButton_1 = new JButton("Help ");
  static String[][]file_list;
  public gui(String[][]file_list1,Socket cSocket) {
	  file_list=file_list1;
    String[]myfile=new String[file_list.length];
    model = new DefaultListModel();
    list = new JList(model);
    list.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.ORANGE, Color.LIGHT_GRAY, Color.RED, Color.YELLOW));
   
    list.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
            JList list = (JList)evt.getSource();
            if (evt.getClickCount() == 2) {
            	try {
        			if(file_list[list.getSelectedIndex()][1].contains("Folder")){
        			Client.Open_Folder(cSocket, file_list[list.getSelectedIndex()][0]);
        			file_list=Client.SearchFile(cSocket);
        			String[]myfile1=new String[file_list.length];
        		    for(int i=0;i<file_list.length;i++){
        		    	myfile1[i]=file_list[i][0]+"   "+file_list[i][1];
        		    }
        		    while (model.getSize() > 0){
        		          model.removeElementAt(0);
        		}
        		  for(int i=0;i<myfile1.length;i++){
        		    	model.addElement(myfile1[i]);
        		    }
        			}
        	} catch (IOException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
            }
        }
    });
    for(int i=0;i<file_list.length;i++){
    	myfile[i]=file_list[i][0]+"   "+file_list[i][1];
    	model.addElement(myfile[i]);
    }
    list.setFont(new Font("Tempus Sans ITC", Font.PLAIN, 16));
    list.setBackground(new Color(240, 255, 240));
    list.setForeground(new Color(0, 0, 0));
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(3);
    JScrollPane pane = new JScrollPane(list);
    pane.setBounds(0, 0, 450, 270);
    setLayout(null);
    JButton DowButton = new JButton("Download");
    DowButton.setFont(new Font("Corsiva Hebrew", Font.PLAIN, 13));
    DowButton.setBackground(new Color(255, 250, 205));
    DowButton.setForeground(new Color(0, 0, 0));
    DowButton.setSize(150, 50);
    DowButton.setLocation(150, 270);
        DowButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
        	 try { 
        		 if(!file_list[list.getSelectedIndex()][1].contains("Folder")){
        			   JFileChooser f = new JFileChooser();
        		        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        		        f.showSaveDialog(null);
        		        System.out.println(f.getSelectedFile());
        		        String path=f.getSelectedFile()+"/";
    			Client.DownloadFile(cSocket,file_list[list.getSelectedIndex()][0],path);
        		 }else{
        			 System.out.println("You can not downloader a folder");
        		 }
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
         }
       });
btnNewButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent arg0) {
		try {
			JFileChooser f = new JFileChooser();
	        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	        f.showSaveDialog(null);
	        System.out.println(f.getSelectedFile());
	        String path=f.getSelectedFile()+"/";
			Client.DownloadAll(cSocket,path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
});
btnNewButton_1.setBackground(new Color(224, 255, 255));
btnNewButton_1.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showInputDialog("1.Download a file: You need to select a file and press Download button\n2.Download All files: Press Download All Button\n3.Open a folder: Double Click");
	}
});
btnNewButton_1.setFont(new Font("Corsiva Hebrew", Font.PLAIN, 13));
btnNewButton_1.setBounds(300, 270, 150, 50);
add(btnNewButton_1);
btnNewButton.setFont(new Font("Corsiva Hebrew", Font.PLAIN, 13));
btnNewButton.setBackground(new Color(255, 192, 203));
btnNewButton.setBounds(0, 270, 150, 50);
add(btnNewButton);
add(DowButton);
    add(pane);
    
    JButton btnBackToThe = new JButton("Back to the upper level");
    btnBackToThe.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		try {
    			if(Client.level>0){
				Client.Back(cSocket);
				file_list=Client.SearchFile(cSocket);
				String[]myfile2=new String[file_list.length];
			    for(int i=0;i<file_list.length;i++){
			    	myfile2[i]=file_list[i][0]+"   "+file_list[i][1];
			    }
			    while (model.getSize() > 0){
			          model.removeElementAt(0);
			}
			  for(int i=0;i<myfile2.length;i++){
			    	model.addElement(myfile2[i]);
			    }
    			}else{
    				System.out.println("It is already the first level");
    			}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    	}
    });
    btnBackToThe.setBackground(new Color(0, 128, 0));
    btnBackToThe.setForeground(new Color(0, 0, 0));
    pane.setColumnHeaderView(btnBackToThe);
  }
}