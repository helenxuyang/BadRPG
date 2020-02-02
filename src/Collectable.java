import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Collectable {

	private int row;
	private int col;
	private Image image;
	private Image centeredImage;
	private int type;
	private int biome;
	private Rectangle rectangle;
	private int numToMake;

	public static final int BANANA = 0;
	public static final int CARROT = 1;
	public static final int MANGO = 2;
	public static final int PINEAPPLE = 3;
	public static final int SEASHELL = 4;
	public static final int STICK = 5;
	public static final int FISHROCK = 6;
	public static final int ACORN = 7;
	public static final int BONE = 8;
	public static final int CHEESE = 9;
	public static final int SUNFLOWER = 10;
	public static final int YARN = 11;

	public static Collectable[] getAllCollectables() {
		int numCollectables = 12;
		Collectable[] collectables = new Collectable[numCollectables];
		for (int i = 0; i < numCollectables; i++) {
			collectables[i] = new Collectable(i);
		}
		return collectables;
	}
	public Collectable(int index) {
		type = index;
		String imageName = "";
		switch(index) {
		case BANANA: 
			imageName = "banana";
			biome = WorldBuilder.RAINFOREST;
			numToMake = 8;
			break;
		case CARROT:
			biome = WorldBuilder.TOWN;
			imageName = "carrot";
			numToMake = 3;
			break;
		case MANGO:
			imageName = "mango";
			biome = WorldBuilder.RAINFOREST;
			numToMake = 8;
			break;
		case PINEAPPLE:
			imageName = "pineapple";
			biome = WorldBuilder.RAINFOREST;
			numToMake = 8;
			break;
		case SEASHELL:
			biome = WorldBuilder.OCEAN;
			imageName = "seashell";
			numToMake = 3;
			break;
		case STICK:
			biome = WorldBuilder.GRASS;
			imageName = "stick";
			numToMake = 3;
			break;
		case FISHROCK:
			biome = WorldBuilder.DESERT;
			imageName = "fishRock";
			numToMake = 1;
			break;
		case ACORN:
			biome = WorldBuilder.TOWN;
			imageName = "acorn";
			numToMake = 10;
			break;
		case BONE:
			biome = WorldBuilder.TOWN;
			imageName = "bone";
			numToMake = 5;
			break;
		case CHEESE:
			biome = WorldBuilder.TOWN;
			imageName = "cheese";
			numToMake = 3;
			break;
		case SUNFLOWER:
			biome = WorldBuilder.TOWN;
			imageName = "sunflower";
			numToMake = 3;
			break;
		case YARN:
			biome = WorldBuilder.TOWN;
			imageName = "yarn";
			numToMake = 3;
			break;
		default:
			break;
		}
		image = new ImageIcon(Main.class.getResource(imageName + ".png")).getImage();
		centeredImage = new ImageIcon(Main.class.getResource(imageName + "Centered.png")).getImage();
	}

	public Image getImage() {
		return image;
	}

	public Image getCenteredImage() {
		return centeredImage;
	}

	public void setRow(int r) {
		row = r;
	}

	public void setCol(int c) {
		col = c;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getType() {
		return type;
	}

	public int getBiome() {
		return biome;
	}

	public int getNumToMake() {
		return numToMake;
	}

	public Rectangle getRect() {
		return rectangle;
	}

	public void setRect(Rectangle r) {
		rectangle = r;
	}

	public String toString() {
		return type + " at " + row + " " + col;
	}

	public boolean equals(Collectable other) {
		return (type == other.type);
	}
}