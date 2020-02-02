import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Interactive {

	private int row;
	private int col;
	private Image image;
	private int biome;
	private Rectangle rectangle;
	private boolean dialogueVisible = false;
	
	public Interactive(String imageName, int row, int col, int biome) {
		image = new ImageIcon(Interactive.class.getResource(imageName + ".png")).getImage();
		this.row = row;
		this.col = col;
		this.biome = biome;
	}
	
	public void showDialogue() {
		dialogueVisible = true;
	}
	
	public void hideDialogue() {
		dialogueVisible = false;
	}
	
	public boolean dialogueVisible() {
		return dialogueVisible;
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public Rectangle getRect() {
		return rectangle;
	}

	public void setRect(Rectangle r) {
		rectangle = r;
	}
	
}
