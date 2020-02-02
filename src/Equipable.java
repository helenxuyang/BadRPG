import java.awt.Image;

import javax.swing.ImageIcon;

public enum Equipable {

	BOOTS("boots"),
	BUGSPRAY("bugspray"),
	COAT("coat"),
	FLASHLIGHT("flashlight"),
	GOGGLES("goggles");
	
	public static final int BOOTSINDEX = 0;
	public static final int BUGSPRAYINDEX = 1;
	public static final int COATINDEX = 2;
	public static final int FLASHLIGHTINEX = 3;
	public static final int GOGGLESINDEX = 4;
	
	private String name;
	private Image image;
	
	public static Equipable[] ALLEQUIPABLES = {BOOTS, BUGSPRAY, COAT, FLASHLIGHT, GOGGLES};

	public static Equipable getEquipable(int index) {
		return ALLEQUIPABLES[index];
	}
	
	private Equipable(String imageName) {
		name = imageName;
		image = new ImageIcon(Collectable.class.getResource(imageName + ".png")).getImage();
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getIndex() {
		int index = -1;
		for (int i = 0; i < ALLEQUIPABLES.length; i++) {
			if (ALLEQUIPABLES[i] == this) {
				index = i;
			}
		}
		return index;
	}
	
	public String toString() {
		return name;
	}
}