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

public class Intro extends JPanel implements KeyListener {
	
	private JFrame frame;
	private static final int SCREENWIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREENHEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int IMAGESIZE = SCREENHEIGHT;
	private Image pressRight = new ImageIcon(Main.class.getResource("/pressRight.gif")).getImage();
	String[] introText = {"Once upon a time, you were an",
			"You were a world famous adventurer and dragon slayer who protected the world.",
			"But one day, a sorcerer cursed you and transported you to the world of...",
			""};
	private Image[] images;
	private int introIndex=0;
	
	Intro (JFrame frame) {
		this.frame = frame;
		frame.addKeyListener(this);
		frame.setContentPane(this);
		frame.setVisible(true);
		images = new Image[4];
		for (int i = 0; i < 4; i++) {
			images[i] = new ImageIcon(Main.class.getResource("/intro" + i + ".png")).getImage();
		}
		this.setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Dialog", Font.BOLD, (int)((float)SCREENWIDTH / 80f)));
		g.drawImage(images[introIndex], (int)((float)SCREENWIDTH / 2f) - (int)((float)IMAGESIZE / 2f), 0, IMAGESIZE, IMAGESIZE, this);
		String text = introText[introIndex];
		int textWidth = g.getFontMetrics().stringWidth(text);
		int textX = (int)((float)SCREENWIDTH / 2f) - (int)((float)textWidth / 2f);
		int textY = (int)((float)SCREENHEIGHT / 10f);
		g.drawString(introText[introIndex], textX, textY);
		
		g.setColor(new Color(255, 255, 255, 50));
		g.fillRect(textX, textY - Game.BLOCKSIZE, textWidth, Game.BLOCKSIZE*2);
		
		g.drawImage(pressRight, SCREENWIDTH - 4*Game.BLOCKSIZE, SCREENHEIGHT - 4*Game.BLOCKSIZE, Game.BLOCKSIZE*2, Game.BLOCKSIZE*2, this);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 39) {
			if (introIndex < introText.length - 1) {
			introIndex++;
			}
			else {
				frame.invalidate();
				frame.removeKeyListener(this);
				Game g = new Game(frame);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}