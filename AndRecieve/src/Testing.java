import java.awt.AWTException;
import java.awt.Robot;


public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// These coordinates are screen coordinates
			int xCoord = 500;
			int yCoord = 500;

			// Move the cursor
			Robot robot = new Robot();
			robot.mouseMove(xCoord, yCoord);
			// ROBOT BABY !
		} catch (AWTException e) {

		}


	}

}
