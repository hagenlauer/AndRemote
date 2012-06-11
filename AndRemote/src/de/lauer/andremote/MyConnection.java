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
import android.util.Log;


public class MyConnection {
	
	private final String tag = this.getClass().getSimpleName();
        /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    //private BufferedReader fromServer;
    //private DataOutputStream toServer;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /* application context */
    Context ctx;

    //private static final String CRLF = "\r\n";

    /* Create an SMSConnection object. Create the socket and the 
       associated streams. Initialize SMS connection. */
    public MyConnection(Context ctx, String host, int port) throws IOException {
        this.ctx=ctx;
        if(this.open(host,port)){
        	//fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //toServer = new DataOutputStream(connection.getOutputStream());
            oos = new ObjectOutputStream(connection.getOutputStream());
            //ois = new ObjectInputStream(connection.getInputStream());
            
        }
        /* may anticipate problems with readers being initialized before connection is opened? */
    }

    public boolean open(String host, int port) {
        try {
            connection = new Socket(host, port);
            return true;
        } catch(IOException e) {
            //Log.i("wifi", "cannot open connection: " + e.toString());
        }
        return false;
    }

    /* Close the connection. */
    public void close() {
        try {
        	ois.close();
        	oos.close();
            connection.close();
        } catch (Exception e) {
            //Log.i(tag,"Unable to close connection: " + e.toString());
        }
    }

    /* Send an SMS command to the server. Check that the reply code
       is what is is supposed to be according to RFC 821. */
    public void sendCommand(String command) throws IOException {

        /* Write command to server. */


        /* read reply */
        //String reply = this.fromServer.readLine();
        try{
        	this.oos.writeObject(command);
        	
            //this.toServer.writeBytes(command+this.CRLF);
        	//Toast.makeText(ctx, reply, Toast.LENGTH_SHORT);
        }catch (Exception e) {
        	((Activity)ctx).setResult(10);
        	((Activity)ctx).finish();
        	//Log.e(tag, "",e);
		}
    }
}
