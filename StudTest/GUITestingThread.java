import java.net.Socket;
import java.io.*;

public class GUITestingThread extends Thread{
	private Socket socket;
	
	public GUITestingThread(Socket socket) {
		this.socket = socket;
		this.start();
	}
	
	public void run() {
		
	}
}
