import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	private static JFrame frame;
	
	public static void main(String[] args) {
		frame = new JFrame("Bad RPG");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setUndecorated(true);

		ImageIcon img = new ImageIcon("./Pictures/icon.png");
		frame.setIconImage(img.getImage());

		Intro i = new Intro(frame);
		/*frame.invalidate();
		Game g = new Game(frame);*/
	}

}