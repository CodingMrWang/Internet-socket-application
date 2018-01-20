import java.io.IOException;
import java.net.Socket;
public class ChildThread extends Thread{
	Socket socket;
	public ChildThread(Socket socket) throws IOException{
		this.socket=socket;
	}
	public void run(){
		try{
			Server.Action(socket);
		}catch(IOException e){
			;
		}
}
}
