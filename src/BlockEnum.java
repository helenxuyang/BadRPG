import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public enum BlockEnum {
	DIRT ("dirt"),
	GRASS ("grass"),
	SAND ("sand"),
	SNOW ("snow"),
	WATER ("water"),
	TREETRUNK ("treetrunk"),
	TREELEAF ("treeleaf");
	//CACTUS ("cactus");
	/*BRICK ("brick"),
	ROOF1 ("roof1"),
	ROOF2 ("roof2"),
	ROCK ("rock"),
	WATER ("water")*/
	
	private final BufferedImage image;
	private char type = 'X';

	private BlockEnum(String name) {
		
		Image img = (new ImageIcon(Main.class.getResource("/" + name + ".png")).getImage());
		image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g = image.createGraphics();
	    g.drawImage(img, 0, 0, null);
	    g.dispose();

		type = name.charAt(0);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public String toString() {
		return "" + type;
	}
}