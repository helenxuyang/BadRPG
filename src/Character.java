import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Character {
	
	private Image currentImage;
	private double health = 100;
	public static final int STAND = 0;
	
	private int[] inventory = new int[Collectable.getAllCollectables().length];
	private Equipable[] equipables = new Equipable[Equipable.ALLEQUIPABLES.length]; //Equipable.ALLEQUIPABLES; 
	private Equipable currentItem;
	private Image[] charImages = new Image[30];
	private ArrayList<String> charImageIndex = new ArrayList<String>();
	private boolean[] hasKeys = new boolean[5];
	
	public Character() {
		initImages();
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = 0;
		}
	}
	
	private void initImages() {	
		charImages[0] = new ImageIcon(Main.class.getResource("/charStand.gif")).getImage();
		charImageIndex.add("stand");
		charImages[1] = new ImageIcon(Main.class.getResource("/charRunRight.gif")).getImage();
		charImageIndex.add("runR");
		charImages[2] = new ImageIcon(Main.class.getResource("/charRunLeft.gif")).getImage();
		charImageIndex.add("runL");
		charImages[3] = new ImageIcon(Main.class.getResource("/charJumpRight.gif")).getImage();
		charImageIndex.add("jumpR");
		charImages[4] = new ImageIcon(Main.class.getResource("/charJumpLeft.gif")).getImage();
		charImageIndex.add("jumpL");
		
		charImages[5] = new ImageIcon(Main.class.getResource("/charStandBoots.gif")).getImage();
		charImageIndex.add("standboots");
		charImages[6] = new ImageIcon(Main.class.getResource("/charRunRightBoots.gif")).getImage();
		charImageIndex.add("runRboots");
		charImages[7] = new ImageIcon(Main.class.getResource("/charRunLeftBoots.gif")).getImage();
		charImageIndex.add("runLboots");
		charImages[8] = new ImageIcon(Main.class.getResource("/charJumpRightBoots.gif")).getImage();
		charImageIndex.add("jumpRboots");
		charImages[9] = new ImageIcon(Main.class.getResource("/charJumpLeftBoots.gif")).getImage();
		charImageIndex.add("jumpLboots");
		
		charImages[10] = new ImageIcon(Main.class.getResource("/charStandBugspray.gif")).getImage();
		charImageIndex.add("standbugspray");
		charImages[11] = new ImageIcon(Main.class.getResource("/charRunRightBugspray.gif")).getImage();
		charImageIndex.add("runRbugspray");
		charImages[12] = new ImageIcon(Main.class.getResource("/charRunLeftBugspray.gif")).getImage();
		charImageIndex.add("runLbugspray");
		charImages[13] = new ImageIcon(Main.class.getResource("/charJumpRightBugspray.gif")).getImage();
		charImageIndex.add("jumpRbugspray");
		charImages[14] = new ImageIcon(Main.class.getResource("/charJumpLeftBugspray.gif")).getImage();
		charImageIndex.add("jumpLbugspray");
		
		charImages[15] = new ImageIcon(Main.class.getResource("/charStandCoat.gif")).getImage();
		charImageIndex.add("standcoat");
		charImages[16] = new ImageIcon(Main.class.getResource("/charRunRightCoat.gif")).getImage();
		charImageIndex.add("runRcoat");
		charImages[17] = new ImageIcon(Main.class.getResource("/charRunLeftCoat.gif")).getImage();
		charImageIndex.add("runLcoat");
		charImages[18] = new ImageIcon(Main.class.getResource("/charJumpRightCoat.gif")).getImage();
		charImageIndex.add("jumpRcoat");
		charImages[19] = new ImageIcon(Main.class.getResource("/charJumpLeftCoat.gif")).getImage();
		charImageIndex.add("jumpLcoat");
		
		charImages[20] = new ImageIcon(Main.class.getResource("/charStandFlashlight.gif")).getImage();
		charImageIndex.add("standflashlight");
		charImages[21] = new ImageIcon(Main.class.getResource("/charRunRightFlashlight.gif")).getImage();
		charImageIndex.add("runRflashlight");
		charImages[22] = new ImageIcon(Main.class.getResource("/charRunLeftFlashlight.gif")).getImage();
		charImageIndex.add("runLflashlight");
		charImages[23] = new ImageIcon(Main.class.getResource("/charJumpRightFlashlight.gif")).getImage();
		charImageIndex.add("jumpRflashlight");
		charImages[24] = new ImageIcon(Main.class.getResource("/charJumpLeftFlashlight.gif")).getImage();
		charImageIndex.add("jumpLflashlight");
		
		charImages[25] = new ImageIcon(Main.class.getResource("/charStandGoggles.gif")).getImage();
		charImageIndex.add("standgoggles");
		charImages[26] = new ImageIcon(Main.class.getResource("/charRunRightGoggles.gif")).getImage();
		charImageIndex.add("runRgoggles");
		charImages[27] = new ImageIcon(Main.class.getResource("/charRunLeftGoggles.gif")).getImage();
		charImageIndex.add("runLgoggles");
		charImages[28] = new ImageIcon(Main.class.getResource("/charJumpRightGoggles.gif")).getImage();
		charImageIndex.add("jumpRgoggles");
		charImages[29] = new ImageIcon(Main.class.getResource("/charJumpLeftGoggles.gif")).getImage();
		charImageIndex.add("jumpLgoggles");

	}
	
	public void setHealth(double inputHealth) {
		health = inputHealth;
	}
	
	public double getHealth() {
		return health;
	}

	public void reduceNum(int type, int num) {
		inventory[type] -= num;
	}

	public void setImage(String direction, String action) {
		int imageIndex;
		if(currentItem!=null) {
			//System.out.println(action+direction+currentItem);
			imageIndex = charImageIndex.indexOf(action+direction+currentItem);
			//System.out.println("image: " + imageIndex);
		}
		else {
			imageIndex = charImageIndex.indexOf(action+direction);
		}
		currentImage=charImages[imageIndex];
	}
	
	public Image getImage() {
		return currentImage;
	}
	
	public void addToInventory(Collectable c) {
		inventory[c.getType()]++;
	}
	
	public void addToInventory(Equipable e) {
		equipables[e.getIndex()] = e;
	}
	
	public int numOf(Collectable c) {
		return inventory[c.getType()];
	}
	
	public boolean has(int numOf, int collectableIndex) {
		if (inventory[collectableIndex] >= numOf) {
			return true;
		}
		return false;
	}
	
	public void addKey(int index) {
		hasKeys[index] = true;
	}
	
	public boolean hasKey(int index) {
		return hasKeys[index];
	}
	
	public boolean hasAllKeys() {
		for (int i = 0; i < hasKeys.length; i++) {
			if (!hasKeys[i]) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasAllFruits() {
		return (inventory[Collectable.BANANA] >= 3 && inventory[Collectable.MANGO] >= 3 && inventory[Collectable.PINEAPPLE] >= 3);
	}
	
	public boolean hasAllSnowmanParts() {
		return (inventory[Collectable.CARROT] >= 1 && inventory[Collectable.STICK] >= 2 && inventory[Collectable.SEASHELL] >= 2);
	}
	
	public boolean hasFishRock() {
		return (inventory[Collectable.FISHROCK] >= 1);
	}
	
	public boolean has(Equipable e) {
		for (int i = 0; i < equipables.length; i++) {
			if (equipables[i] != null && equipables[i] == e) {
				return true;
			}
		}
		return false;
	}
	
	public void setEquippedItem(Equipable e) {
		currentItem = e;
	}
	
	public Equipable getCurrentItem() {
		return currentItem;
	}
}