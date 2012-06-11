import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;


public final class MyHandler implements Runnable {
	//
	final static String CRLF = "\r\n";
	Socket socket;
	Robot robot;

	private boolean masterFlag = false;
	/*
	 * keep multiple sceens im mind 
	 */

	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	int maxx = dim.width;
	int maxy = dim.height;

	Point mouse;

	String [] split;

	boolean flag = true;

	// Constructor
	public MyHandler(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try {
			robot = new Robot();
			processRequest();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
	{
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while((bytes = fis.read(buffer)) != -1 ) {
			os.write(buffer, 0, bytes);
		}
	}

	private void processRequest() throws Exception
	{
		// Get a reference to the socket's input and output streams.
		InputStream is = this.socket.getInputStream();
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());

		// Set up input stream filters.
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		//FileInputStream fis = this.socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());  

		// Get the request line of the SMS request message.
		String requestLine = "";
		while(flag){

			requestLine = (String)ois.readObject();

			//System.out.println("hope this doesnt show");

			if(requestLine.equalsIgnoreCase("kthxbye")){
				System.out.println("closing connection");
				os.close();
				br.close();
				socket.close();
				flag =!flag;
				AndRecieve.decrementConnectionCoutn();
				if(!masterFlag)break;
				System.out.println("Good bye, Master!");
				try{
					Runtime.getRuntime().exec("say -v Daniel \"Good bye sir!\""); //will work on my mac, fuck the rest.
				}catch(Exception e){
					//its not a mac, i dont care.
				}
				break;
			}

			if(requestLine.startsWith("pass:")){
				String pass = requestLine.substring(5, requestLine.length());
				//System.out.println("passphrase: "+pass);
				if(pass.equals(AndRecieve.getPass())){
					System.out.println("Welcome, Master!");
					try{
						Runtime.getRuntime().exec("say -v Daniel \"Welcome back sir!\""); //will work on my mac, fuck the rest.
					}catch(Exception e){
						//its not a mac, i dont care.
					}
					//JOptionPane.showMessageDialog(null, "Nice to see you, Sir!", "Welcome Hagen!", JOptionPane.INFORMATION_MESSAGE,null);
					masterFlag = true;
					continue;
				}
			}


			switch (requestLine) {
			case "ohi":
				break;
			case "imback":
				break;
			case "left":
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case "leftup":
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case "right":
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				break;
			case "rightup":
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			case "arrowup":
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_UP);
				break;
			case "arrowdown":
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);
				break;
			case "arrowleft":
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				break;
			case "arrowright":
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				break;
			default:
				split = requestLine.split(":");
				int x = Integer.valueOf(split[0]);
				int y = Integer.valueOf(split[1]);
				mouse = MouseInfo.getPointerInfo().getLocation();
				moveMouse(x, y, mouse);
				break;
			}

		}
	}
	private boolean moveMouse(int x, int y, Point mouse){

		int resx = mouse.x+x;
		int resy = mouse.y+y;

		if(resx <= 0){
			robot.mouseMove(maxx-5, resy);
			return true;
		}
		if(resx >= maxx){
			robot.mouseMove(0+5, resy);
			return true;
		}
		if(resy <= 0){
			robot.mouseMove(resx, maxy-5);
			return true;
		}
		if(resy >= maxy){
			robot.mouseMove(resx, 0+5);
			return true;
		}
		robot.mouseMove(resx, resy);
		return true;
	}
}
