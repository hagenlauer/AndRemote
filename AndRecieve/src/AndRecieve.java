import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class AndRecieve {
	///////MEMBER VARIABLES
	ServerSocket server=null;
	Socket client=null;

	private static final String pass = "youexpectedapass?tellmeeverythingaboutthat"; //read it from a pass file, please.
	
	private static Object lock = new Object();

	private static int connectioncount = 0;

	///////MEMBER FUNCTIONS
	public boolean createSocket(int port) {
		try{
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port "+port);
			System.exit(-1);
		}
		return true;
	}
	
	public static String getPass(){
		return pass;
	}
	
	public boolean listenSocket(){
		try{
			client = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: ");
			System.exit(-1);
		}
		return true;
	}

	public static void decrementConnectionCoutn(){
		synchronized (lock) {
			--connectioncount;
		}
		System.out.println("decr conn");
	}
	public static int readConnectionCount(){
		System.out.println("read conn");
		synchronized (lock) {
			return connectioncount;
		}
	}
	public static void incrementConnectionCount(){
		synchronized (lock) {
			++connectioncount;
		}
		System.out.println("incr conn");
	}

	public static void main(String argv[]) throws Exception {
		//
		AndRecieve mySock = new AndRecieve();
		try {
			InetAddress local = InetAddress.getLocalHost();
			System.out.println(local.getHostAddress()+":"+local.getHostName());
			JOptionPane.showMessageDialog(null, local.getHostAddress(), "Your IP Adress, sir", JOptionPane.INFORMATION_MESSAGE,null);
		} catch (Exception e) {
			//JOptionPane.showMessageDialog(null, "Yes, you did, not me.", "You messed something up", JOptionPane.INFORMATION_MESSAGE,null);
		}
		//establish the listen socket
		mySock.createSocket(3005);
		while(true) {

			if(mySock.listenSocket() && readConnectionCount() == 0) { //sync problem

//				if(JOptionPane.showConfirmDialog(null, "Would you like to accept "+mySock.client.getInetAddress()+" ?") == JOptionPane.OK_OPTION || 1==1){

					incrementConnectionCount();

					//make new thread
					// Construct an object to process the SMS request message.
					System.out.println(mySock.client.getInetAddress());
					MyHandler request = new MyHandler(mySock.client);

					// Create a new thread to process the request.
					Thread thread = new Thread(request);

					// Start the thread.
					thread.start();
//				}else{
//					System.out.println("conncetion refused");
//				}
			}

		}
	}
}
