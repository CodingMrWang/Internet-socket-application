import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.*;

	public class Server extends JFrame{ 
		public static String Address;
		public static int Num_of_file=0;
		static private int Password;
	public static String[] Name_List = new String[100];
		public Server(int port) throws IOException {

		}
     public static void FileList(Socket cSocket) {      
			      File f = null;
			      String[] paths;
			      
			      try {    
			    	  DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
			    	  out.writeInt(1);
			         // create new file
			    	  	System.out.println(Address);
			         f = new File(Address);               
			         // array of files and directory
			         
			         paths = f.list();
			         // for each name in the path array
			         out.writeInt(paths.length);
			         for(String path:paths) {
			            // prints filename and directory name
			            out.writeInt(path.length());
			        	out.write(path.getBytes());
			        	File file=new File(Address+path);
			        	Path file1= new File(Address+path).toPath();
			        	int File_Size=(int)file.length();
			        	
			        	if(Files.isRegularFile(file1)){
			        		out.writeInt(File_Size);
			        		Name_List[Num_of_file]=path;
			        		Num_of_file++;
			        	}else{
			        		out.writeInt(-1);
			        	}
			         }
			      } catch(Exception e) {
			         // if any error occurs
			         e.printStackTrace();
			      }
			   }
     public static void OpenFolder(Socket cSocket,String File_Path){
    	 Num_of_file=0;
    	 Address=Address+File_Path+"/";
     }
     public static void Back_Level(Socket cSocket,String File_Path){
    	 Num_of_file=0;
    	 Address=Address.replace(File_Path+"/", "");
     }
		public static void Send(Socket cSocket) throws IOException {
			DataInputStream inn = new DataInputStream(cSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
			out.writeInt(2);
			int Name_Size=inn.readInt();
		    byte[] data=new byte[1024];
			inn.read(data,0,Name_Size);
			String File_send=new String(data,0,Name_Size);
			System.out.println(Address);
			String File_Name=Address+File_send;
		    File file=new File(File_Name);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			long size;
			int len;
			long counter=0; 
		    size =(long)file.length();
		    System.out.println(size);
		   	  out.writeLong(size);//sent the size of the file

		    while(counter<size){                 //sent file
		    	if(size-counter>1024){
		    		System.out.println(counter);
		    		System.out.println(size);
				    len=in.read(data,0,1024);
					}else{
						int a=(int)(size-counter);
						System.out.println(counter);
			    		System.out.println(size);
						len=in.read(data,0,a);
					}
		     out.write(data,0,len);
		     counter+=len;
		     out.flush();
		    }	
		    System.out.println(counter);
			//in.close();
			
		}
		public static void SendAll(Socket cSocket) throws IOException {
			DataInputStream inn = new DataInputStream(cSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
			out.writeInt(Num_of_file);
            for(int i=0;i<Num_of_file;i++){
            	 out.writeInt(Name_List[i].length());
		        	out.write(Name_List[i].getBytes());
            }
		}
		public static void Action(Socket cSocket) throws IOException, EOFException{
			Num_of_file=0;
			
			byte[]buffer=new byte[1024];
			while(true){
			DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
			DataInputStream in = new DataInputStream(cSocket.getInputStream());
			boolean stop_return=false;
			
			for(int i=0;i<3;i++){      //allow client try password three times.
				int pass_word=in.readInt();
			if(pass_word==Password){
				out.writeInt(987654);  //successful password, return an ack to tell client to continue
				break;
			}else{
				out.writeInt(-1);
				if(i==2){
					cSocket.close();   //password input fail three times, so stop the socket connection 
				    stop_return=true;
				}
			}
			}
			if(stop_return==true) continue;
			System.out.println(
					String.format("Established connection to client %s:%d", 
							cSocket.getInetAddress().getHostAddress(), cSocket.getPort()));
			String[] File_Path=new String[20];
			int i=0;
			int option=0;
			while(option!=-1){
				
				if (in.available() <= 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
					continue;
				}
				option=in.readInt();
				switch(option){
				case 1:
					FileList(cSocket);
					break;
				case 2:
					Send(cSocket);
					break;
				case 3:
					int Name_Size=in.readInt();
					in.read(buffer,0,Name_Size);
					String FileName=new String(buffer,0,Name_Size);
					File_Path[i]=FileName;
					OpenFolder(cSocket,File_Path[i]);
					i++;
					break;
				case 4:
					i--;
					Back_Level(cSocket,File_Path[i]);
					break;
				case 5:
					SendAll(cSocket);
				}
		}
			}
	}
	
		public static void main(String[]args)throws IOException, EOFException{
		ServerSocket sSocket = new ServerSocket(8999);
		JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        f.showSaveDialog(null);
        Address=f.getSelectedFile()+"/";
		Password = Integer.parseInt(JOptionPane.showInputDialog("Set a password"));
		
		while(true){
		System.out.printf("Listening to TCP port# %d...\n",
				sSocket.getLocalPort());
		Socket cSocket = sSocket.accept();
		ChildThread thread=new ChildThread(cSocket);
		thread.start();
		
	}
		}

	}

