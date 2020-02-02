import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public enum NPC {

	BATTHEW("batthew", WorldBuilder.GRASS, Quest.GRASSKEY, Game.BLOCKSIZE * 7, Game.BLOCKSIZE * 6, 0.5),
	SNAKES("snakes", WorldBuilder.RAINFOREST, Quest.RAINFORESTKEY, Game.BLOCKSIZE * 4, Game.BLOCKSIZE * 2, 0.25),
	DONKEYS("donkeys", WorldBuilder.DESERT, Quest.RAINFORESTKEY, Game.BLOCKSIZE * 8, Game.BLOCKSIZE * 4, 0.5),
	DOVEKEYS("dovekeys", WorldBuilder.OCEAN, Quest.RAINFORESTKEY, Game.BLOCKSIZE * 3, Game.BLOCKSIZE * 3, 0.07),
	MONKEYS("monkeys", WorldBuilder.RAINFOREST, Quest.RAINFORESTKEY, Game.BLOCKSIZE * 5, Game.BLOCKSIZE * 8, 0.75),
	TURKEYS("turkeys", WorldBuilder.TOWN, Quest.RAINFORESTKEY, Game.BLOCKSIZE * 6, Game.BLOCKSIZE * 3, 0.7),
	SNOWMAN("snowman", WorldBuilder.SNOW, Quest.SNOWKEY, Game.BLOCKSIZE * 3, Game.BLOCKSIZE * 3, 0.5),
	FISH("fish", WorldBuilder.OCEAN, Quest.OCEANKEY, Game.BLOCKSIZE * 2, Game.BLOCKSIZE * 2, 0.5),
	BUSH("bush", WorldBuilder.DESERT, Quest.DESERTKEY, Game.BLOCKSIZE * 3, Game.BLOCKSIZE * 3, 0.25),
	CACTUS("cactus", WorldBuilder.DESERT, Quest.DESERTKEY, Game.BLOCKSIZE * 3, Game.BLOCKSIZE * 3, 0.75),
	CAT("cat", WorldBuilder.TOWN, Quest.SNOWITEM, Game.BLOCKSIZE * 4, Game.BLOCKSIZE * 2, 0.1),
	DOG("dog", WorldBuilder.TOWN, Quest.DESERTITEM, Game.BLOCKSIZE * 4, Game.BLOCKSIZE * 2, 0.2),
	MOUSE("mouse", WorldBuilder.TOWN, Quest.RAINFORESTITEM, Game.BLOCKSIZE * 2, Game.BLOCKSIZE * 2, 0.3),
	BIRD("bird", WorldBuilder.TOWN, Quest.OCEANITEM, Game.BLOCKSIZE * 2, Game.BLOCKSIZE * 2, 0.8),
	SQUIRREL("squirrel", WorldBuilder.TOWN, Quest.GRASSITEM, Game.BLOCKSIZE * 2, Game.BLOCKSIZE * 2, 0.9);
	
	private String name;
	private Image image; 
	private int row;
	private int col;
	private int colInBiome;
	private int biome;
	private int width;
	private int height;
	private int blocksWide;
	private Rectangle rectangle;
	private boolean dialogueVisible = false;
	private DialogueBlock dBlock = null;
	private boolean talked = false;
	private boolean interactable = false;
	private Quest quest;
	public static final int BATTHEWINDEX = 0;
	public static final int SNAKESINDEX = 1;
	public static final int DONKEYSINDEX = 2;
	public static final int DOVEKEYSINDEX = 3;
	public static final int MONKEYSINDEX = 4;
	public static final int TURKEYSINDEX = 5;
	
	private NPC(String name, int biome, Quest quest, int width, int height, double pos) {
		if (name.equals("batthew")) {
			image = new ImageIcon(Main.class.getResource("/caveClosed.png")).getImage();
			interactable = false;
		}
		else {
			image = new ImageIcon(Main.class.getResource("/" + name + ".gif")).getImage();
			interactable = true;
		}
		this.name = name;
		this.biome = biome;
		this.quest = quest;
		this.width = width;
		this.height = height;
		blocksWide = width / Game.BLOCKSIZE;
		colInBiome = (int)(pos * (float)WorldBuilder.BIOMEWIDTH);
		//System.out.println("x in biome " + colInBiome);
	}
	
	public static NPC[] getNPCs() {
		return new NPC[] {BATTHEW, SNAKES, DONKEYS, DOVEKEYS, MONKEYS, TURKEYS, SNOWMAN, FISH, BUSH, CACTUS, CAT, DOG, MOUSE, BIRD, SQUIRREL};
	}
	
	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBiome() {
		return biome;
	}

	public Quest getQuest() {
		return quest;
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

	public int getColInBiome() {
		return colInBiome;
	}
	
	public int getBlocksWide() {
		return blocksWide;
	}
	
	public boolean isInteractable() {
		return interactable;
	}
	
	public boolean talkedTo() {
		return talked;
	}
	
	public void setImage(Image i) {
		image = i;
	}
	
	public void setRow(int r) {
		row = r;
	}

	public void setCol(int c) {
		col = c;
	}

	public void setInteractable(boolean bool) {
		interactable = bool;
	}
	public void setTalkedTrue() {
		talked = true;
	}
	
	public Rectangle getRect() {
		return rectangle;
	}

	public void setRect(Rectangle r) {
		rectangle = r;
	}

	public boolean dialogueVisible() {
		return dialogueVisible;
	}

	public void showDialogue() {
		dialogueVisible = true;
	}

	public void hideDialogue() {
		dialogueVisible = false;
	}

	public void setDialogueBlock(DialogueBlock d) {
		dBlock = d;
	}

	public DialogueBlock getDialogueBlock() {
		return dBlock;
	}

	public String toString() {
		return name;
	}
	
	public boolean equals(NPC other) {
		return other.getName().equals(getName());
	}
}