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

	int maxx;// = dim.width;
	int maxy;// = dim.height;

	boolean metapressed = false;

	Point mouse;

	String system = System.getProperty("os.name");
	
	
	String [] split;

	boolean flag = true;
	boolean sysflag = false; //mac extrawurst
	// Constructor
	public MyHandler(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try {
			System.out.println("running on "+system);
			
			if(system.equalsIgnoreCase("Mac OS X")){ //mac extrawurst
				sysflag = true;
			}
					
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
				System.out.println("pass recieved");
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

			if(requestLine.startsWith("text:")){
				String s = requestLine.substring(5, requestLine.length());
				if(s.length() < 1) continue;
				try{
					Runtime.getRuntime().exec("say -v Daniel \""+s+"\""); //will work on my mac, fuck the rest.
				}catch(Exception e){
					//its not a mac, i dont care.
				}
				type(s);
				continue;
			}

			switch (requestLine) {
			case "ohi":
				System.out.println("ohi recieved");
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
				robot.keyRelease(KeyEvent.VK_META);

				break;
			case "arrowdown":
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_META);

				break;
			case "arrowleft":
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				break;
			case "arrowright":
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				break;
			case "delkey":
				robot.keyPress(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_DELETE);
				break;
			case "backspace":
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
				break;
			case "enter":
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				break;
			case "alt_tab":
				if(sysflag){
					robot.keyPress(KeyEvent.VK_META);
				}else{
					robot.keyPress(KeyEvent.VK_ALT);
				}
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				metapressed = true;
				continue;
			default:
				dim = Toolkit.getDefaultToolkit().getScreenSize();

				maxx = dim.width;
				maxy = dim.height;

				split = requestLine.split(":");
				int x = Integer.valueOf(split[0]);
				int y = Integer.valueOf(split[1]);
				mouse = MouseInfo.getPointerInfo().getLocation();
				moveMouse(x, y, mouse);
				continue;
			}
			if(metapressed){
				if(sysflag){
					robot.keyRelease(KeyEvent.VK_META);
				}else{
					robot.keyRelease(KeyEvent.VK_ALT);
				}
				metapressed = false;
				continue;
			}

		}
	}
	public void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
                char character = characters.charAt(i);
                type(character);
        }
    }

    public void type(char character) {
        switch (character) {
        case 'a': doType(KeyEvent.VK_A); break;
        case 'b': doType(KeyEvent.VK_B); break;
        case 'c': doType(KeyEvent.VK_C); break;
        case 'd': doType(KeyEvent.VK_D); break;
        case 'e': doType(KeyEvent.VK_E); break;
        case 'f': doType(KeyEvent.VK_F); break;
        case 'g': doType(KeyEvent.VK_G); break;
        case 'h': doType(KeyEvent.VK_H); break;
        case 'i': doType(KeyEvent.VK_I); break;
        case 'j': doType(KeyEvent.VK_J); break;
        case 'k': doType(KeyEvent.VK_K); break;
        case 'l': doType(KeyEvent.VK_L); break;
        case 'm': doType(KeyEvent.VK_M); break;
        case 'n': doType(KeyEvent.VK_N); break;
        case 'o': doType(KeyEvent.VK_O); break;
        case 'p': doType(KeyEvent.VK_P); break;
        case 'q': doType(KeyEvent.VK_Q); break;
        case 'r': doType(KeyEvent.VK_R); break;
        case 's': doType(KeyEvent.VK_S); break;
        case 't': doType(KeyEvent.VK_T); break;
        case 'u': doType(KeyEvent.VK_U); break;
        case 'v': doType(KeyEvent.VK_V); break;
        case 'w': doType(KeyEvent.VK_W); break;
        case 'x': doType(KeyEvent.VK_X); break;
        case 'y': doType(KeyEvent.VK_Y); break;
        case 'z': doType(KeyEvent.VK_Z); break;
        case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
        case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
        case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
        case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
        case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
        case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
        case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
        case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
        case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
        case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
        case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
        case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
        case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
        case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
        case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
        case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
        case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
        case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
        case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
        case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
        case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
        case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
        case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
        case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
        case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
        case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
        case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
        case '0': doType(KeyEvent.VK_0); break;
        case '1': doType(KeyEvent.VK_1); break;
        case '2': doType(KeyEvent.VK_2); break;
        case '3': doType(KeyEvent.VK_3); break;
        case '4': doType(KeyEvent.VK_4); break;
        case '5': doType(KeyEvent.VK_5); break;
        case '6': doType(KeyEvent.VK_6); break;
        case '7': doType(KeyEvent.VK_7); break;
        case '8': doType(KeyEvent.VK_8); break;
        case '9': doType(KeyEvent.VK_9); break;
        case '-': doType(KeyEvent.VK_MINUS); break;
        case '=': doType(KeyEvent.VK_EQUALS); break;
        case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
        case '!': doType(KeyEvent.VK_EXCLAMATION_MARK); break;
        case '@': doType(KeyEvent.VK_AT); break;
        case '#': doType(KeyEvent.VK_NUMBER_SIGN); break;
        case '$': doType(KeyEvent.VK_DOLLAR); break;
        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
        case '^': doType(KeyEvent.VK_CIRCUMFLEX); break;
        case '&': doType(KeyEvent.VK_AMPERSAND); break;
        case '*': doType(KeyEvent.VK_ASTERISK); break;
        case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
        case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
        case '_': doType(KeyEvent.VK_UNDERSCORE); break;
        case '+': doType(KeyEvent.VK_PLUS); break;
        case '\t': doType(KeyEvent.VK_TAB); break;
        case '\n': doType(KeyEvent.VK_ENTER); break;
        case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
        case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
        case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
        case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
        case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
        case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
        case ';': doType(KeyEvent.VK_SEMICOLON); break;
        case ':': doType(KeyEvent.VK_COLON); break;
        case '\'': doType(KeyEvent.VK_QUOTE); break;
        case '"': doType(KeyEvent.VK_QUOTEDBL); break;
        case ',': doType(KeyEvent.VK_COMMA); break;
        case '<': doType(KeyEvent.VK_LESS); break;
        case '.': doType(KeyEvent.VK_PERIOD); break;
        case '>': doType(KeyEvent.VK_GREATER); break;
        case '/': doType(KeyEvent.VK_SLASH); break;
        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
        case ' ': doType(KeyEvent.VK_SPACE); break;
        default:
                throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
                return;
        }

        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
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
