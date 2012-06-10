import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
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
				break;
			}

			switch (requestLine) {
			case "ohi":
				break;
			case "imback":
				break;
			case "left":
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case "right":
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				break;

			default:
				//System.out.println(requestLine);

				split = requestLine.split(":");
				int x = Integer.valueOf(split[0]);
				int y = Integer.valueOf(split[1]);
				
				mouse = MouseInfo.getPointerInfo().getLocation();
				
				//implement the corner stuff here:
				moveMouse(x, y, mouse);
//				if(mouse.y > 10 && mouse.x > 10 && mouse.x < maxx-10 && mouse.y < maxy-10){
//					robot.mouseMove(mouse.x + x, mouse.y + y);
//				}
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
