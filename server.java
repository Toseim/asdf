package doorController;
import java.io.IOException;
import java.net.*;
public class server {

	ServerSocket serverSocket;
	Socket device = null;
	public static void main(String []args){
		server s = new server();
		s.launch();
	}
	
	public void launch(){
		try {
			serverSocket = new ServerSocket(8888);
		} catch (IOException e) {
			System.out.println("端口被占用");
			System.exit(-1);
			e.printStackTrace();
		}
		WatingLine watingLine = new WatingLine(this);
		Thread thread = new Thread(watingLine);
		thread.start();
	}
	
	public ServerSocket getSrvSocket(){
		return this.serverSocket;
	}
}

