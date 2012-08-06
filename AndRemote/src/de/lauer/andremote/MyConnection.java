package de.lauer.andremote;
import java.net.*;
import java.io.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class MyConnection {

	private final String tag = this.getClass().getSimpleName();
	/* The socket to the server */
	private Socket sock;
	private Object obj = new Object();

	private Vector<String> msgStack = new Vector<String>();

	/* Streams for reading and writing the socket */
	//private BufferedReader fromServer;
	//private DataOutputStream toServer;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;

	/* application context */
	Context ctx;

	String host;
	int port;

	//private static final String CRLF = "\r\n";

	/* Create an SMSConnection object. Create the socket and the 
       associated streams. Initialize SMS connection. */
	public MyConnection(Context ctx, String host, int port) throws IOException {
		this.ctx=ctx;
		this.host = host;
		this.port = port;

		open(host, port);

		//if(this.open(host,port)){
		//fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		//toServer = new DataOutputStream(connection.getOutputStream());
		//    oos = new ObjectOutputStream(sock.getOutputStream());
		//ois = new ObjectInputStream(connection.getInputStream());

		//}
		/* may anticipate problems with readers being initialized before connection is opened? */
	}

	public boolean open(String host, int port) {
		sock = new Socket();
		new ConnectSocket().start();
		return true;
	}

	/* Close the connection. */
	public void close() {
		try {
			oos.close();
			sock.close();
		} catch (Exception e) {
			Log.e(tag,"Unable to close connection", e);
		}
	}

	/* Send an SMS command to the server. Check that the reply code
       is what is is supposed to be according to RFC 821. */
	public void sendCommand(String command) throws IOException {

		/* Write command to server. */


		/* read reply */
		//String reply = this.fromServer.readLine();
		try{

			if(oos == null){
				Log.i(tag, "adding to stack: "+command);
				msgStack.add(command);
				return;
			}

			for (String message : msgStack) {
				Log.i(tag, "writing from stack: "+message);
				this.oos.writeObject(message);
				msgStack.remove(message);
			}



			Log.i(tag, "sendCommand("+command+")");
			this.oos.writeObject(command);

			//this.toServer.writeBytes(command+this.CRLF);
			//Toast.makeText(ctx, reply, Toast.LENGTH_SHORT);
		}catch (Exception e) {
			Log.e(tag, "",e);
			((Activity)ctx).setResult(10);
			((Activity)ctx).finish();
			//Log.e(tag, "",e);
		}
	}




	class ConnectSocket extends Thread {
		@Override
		public void run() {
			SocketAddress socketAddress = new InetSocketAddress(host, port);
			try {               
				sock.connect(socketAddress);
				oos = new ObjectOutputStream(sock.getOutputStream());
				Log.i(tag, "connected to: "+ host);
				//Toast.makeText(ctx, "connected", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Log.i(tag, "not connected to: "+ host);
				((Activity)ctx).setResult(10);
				((Activity)ctx).finish();
				//Toast.makeText(ctx, "Not connected", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		}

	}

}
