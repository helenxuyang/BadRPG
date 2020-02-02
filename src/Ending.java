import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ending extends JPanel implements KeyListener {

	JFrame frame;
	private static final int SCREENWIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREENHEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int IMAGESIZE = (int)((float)(SCREENHEIGHT) * (9f/10f));
	String[] endingText = {"You put the final key into the chest and...",
			"are restored to your original heroic self!",
	"And you live happily ever after. :)"};
	private Image pressRight = new ImageIcon(Main.class.getResource("/pressRight.gif")).getImage();
	private Image[] images;
	private int endingIndex=0;

	public static void main(String[] args) {
		Ending e = new Ending(new JFrame());
	}

	Ending (JFrame frame) {
		this.frame=frame;
		frame.addKeyListener(this);
		frame.setContentPane(this);
		frame.setVisible(true);
		images = new Image[3];
		for (int i = 0; i < images.length; i++) {
			images[i] = new ImageIcon(Main.class.getResource("/ending" + i + ".gif")).getImage();
		}
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Dialog", Font.BOLD, (int)((float)SCREENWIDTH / 80f)));
		g.drawImage(images[endingIndex], (int)((float)SCREENWIDTH / 2f) - (int)((float)IMAGESIZE / 2f), 0, IMAGESIZE, IMAGESIZE, this);
		String text = endingText[endingIndex];
		int textWidth = g.getFontMetrics().stringWidth(text);
		int textX = (int)((float)SCREENWIDTH / 2f) - (int)((float)textWidth / 2f);
		g.drawString(endingText[endingIndex], textX, (int)((float)SCREENHEIGHT / 10f));
		if (endingIndex < endingText.length - 1) {
			g.drawImage(pressRight, SCREENWIDTH - 4*Game.BLOCKSIZE, SCREENHEIGHT - 4*Game.BLOCKSIZE, Game.BLOCKSIZE*2, Game.BLOCKSIZE*2, this);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 39) {
			if (endingIndex < endingText.length - 1) {
				endingIndex++;
			}
			else {
				frame.invalidate();
				frame.removeKeyListener(this);
				//Game g = new Game(frame);
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}