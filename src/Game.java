import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;
//https://stackoverflow.com/questions/658059/graphics-drawimage-in-java-is-extremely-slow-on-some-computers-yet-much-faster
//this means something probably idk rip
public class Game extends JPanel implements KeyListener, ActionListener {

	private JFrame frame;
	private Timer timer;
	private BlockEnum[][] world;
	private BufferedImage[][] images;
	private ArrayList<Particle> particles = new ArrayList<Particle>();

	private static final int SCREENWIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREENHEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int BLOCKSWIDE = 40;
	public static final int BLOCKSIZE = SCREENWIDTH / BLOCKSWIDE;
	public static final int HALFBLOCKSIZE = (int)((float)BLOCKSIZE / 2f);
	public static int MOVEINCREMENT = (int)((float)BLOCKSIZE / 3f) * 2;
	public static final int GRAVITY = (int)((float)MOVEINCREMENT / 8f);
	public static final int JUMPVEL = (int)(1.25f * (float)MOVEINCREMENT);
	public static final int WALK_DEACCEL = MOVEINCREMENT/3;
	private static long initialTime;

	private final int charX = BLOCKSIZE * (BLOCKSWIDE / 2);
	private final int charY = SCREENHEIGHT / 2 - BLOCKSIZE;
	private final int xStart = charX - (int)((double)WorldBuilder.BIOMEWIDTH * (double)BLOCKSIZE * 3.5);
	private final int yStart = BLOCKSIZE * WorldBuilder.WORLDHEIGHT;
	private int xPos = xStart;
	private int yPos = yStart;
	private Character character = new Character();
	private Rectangle charRect = new Rectangle(charX, charY, BLOCKSIZE * 2, BLOCKSIZE * 4);
	private int xVel = 0;
	private int yVel = 0;
	private Rectangle belowRect;
	private int fade = 255;
	String charDirection="";
	private boolean moved = false;
	private ArrayList<Integer> keysPressed = new ArrayList<Integer>();

	private Image arrowImage;
	private Image townImage;
	private Image chestImage;
	private Image pressLeftImage = new ImageIcon(Main.class.getResource("/pressLeft.gif")).getImage();
	private Image pressRightImage = new ImageIcon(Main.class.getResource("/pressRight.gif")).getImage();
	private Image pressUpImage = new ImageIcon(Main.class.getResource("/pressUp.gif")).getImage();
	private Image pressMImage;
	private Image[] keyImages;
	private Image[] numImages;
	private Image battOpen;
	private Image battClosed;
	private Image warning;
	private Image snowmanDone;

	private Font titleFont;
	private Font dialogueFont;
	private Font normalFont;
	private Font questFont;

	private int[] surfaceRows = new int[WorldBuilder.WORLDWIDTH];
	private int[][] treePositions;
	private int chestX;
	private int chestY;
	private Rectangle chestRect;
	private NPC[] NPCs = NPC.getNPCs();
	private Collectable[] collectableList = Collectable.getAllCollectables();
	private ArrayList<Collectable> collectables = new ArrayList<Collectable>();
	private Quest[] quests = Quest.getAllQuests();
	private ArrayList<Quest> questList = new ArrayList<Quest>();
	private boolean menuOpen = false;
	private static final int MILLISPERTICK = 100;
	private static final int TICKSPERDAY = 1024;
	private long loopCycles;
	private boolean inputEnabled = true;
	private boolean drawArrows = false;
	private boolean needDrawOuch = false;
	private boolean needDrawMosquito = false;
	private boolean needDrawNotif = false;
	private String notifMessage = "";
	private int notifTime = 0;
	private Color rainColor = new Color(66, 134, 244);

	private BlockEnum[][] tree =
		{{BlockEnum.TREELEAF, BlockEnum.TREELEAF, BlockEnum.TREELEAF},
				{BlockEnum.TREELEAF, BlockEnum.TREELEAF, BlockEnum.TREELEAF},
				{BlockEnum.TREELEAF, BlockEnum.TREELEAF, BlockEnum.TREELEAF},
				{null, BlockEnum.TREETRUNK, null},
				{null, BlockEnum.TREETRUNK, null},
				{null, BlockEnum.TREETRUNK, null}};

	public Game(JFrame frame) {
		this.frame = frame;
		frame.setContentPane(this);
		frame.addKeyListener(this);
		frame.validate();

		initWorld();
		images = getRotatedWorld(world);
		frame.repaint();
		frame.setVisible(true);
		//System.out.println("blocksize: " + BLOCKSIZE);
		initSurfaceRows();
		initCollectables();
		makeTrees();
		initNPCs();
		initImages();
		initFont();

		yPos += distanceBelow();
		timer = new Timer(20, this);
		timer.start();
		initialTime = System.currentTimeMillis();
		//System.out.println("world col " + xToCol(xPos));
	}

	private void initWorld() {
		boolean worldGenerated=false;
		while(!worldGenerated) {
			try{
				world = WorldBuilder.genWorld();
				worldGenerated=true;
			}
			catch(Exception e) {
				System.out.println("INIT WORLD BROKE");
			}
		}
	}
	private void initFont() {
		titleFont = new Font("Helvetica", Font.BOLD, (int)((float)SCREENWIDTH / 80f));
		dialogueFont = new Font("Dialog", Font.PLAIN, (int)((float)SCREENWIDTH / 100f));
		normalFont = new Font("Helvetica", Font.BOLD, (int)((float)SCREENWIDTH / 95f));
	}

	private void initImages() {
		pressLeftImage = new ImageIcon(Main.class.getResource("/pressLeft.gif")).getImage();
		pressRightImage = new ImageIcon(Main.class.getResource("/pressRight.gif")).getImage();
		pressUpImage = new ImageIcon(Main.class.getResource("/pressUp.gif")).getImage();
		arrowImage = new ImageIcon(Main.class.getResource("/pressDown.gif")).getImage();
		townImage = initImage("town");
		chestImage = initImage("chest");
		numImages = new Image[5];
		for (int i = 1; i <= 5; i++) {
			String imageName = "/press" + i + ".gif";
			numImages[i-1] = new ImageIcon(Main.class.getResource(imageName)).getImage();
		}
		keyImages = new Image[5];
		for (int i = 0; i < 5; i++) {
			String imageName = "/key" + i + ".png";
			keyImages[i] = new ImageIcon(Main.class.getResource(imageName)).getImage();
		}
		pressMImage = new ImageIcon(Main.class.getResource("/pressM.gif")).getImage();
		battOpen = new ImageIcon(Main.class.getResource("/caveOpen.png")).getImage();
		battClosed = new ImageIcon(Main.class.getResource("/caveClosed.png")).getImage();
		warning = new ImageIcon(Main.class.getResource("/warning.gif")).getImage();
		snowmanDone = new ImageIcon(Main.class.getResource("/snowmanDone.gif")).getImage();
	}

	private Image initImage(String name) {
		Image i = new ImageIcon(Main.class.getResource("/" + name + ".png")).getImage();
		return i;
	}

	public static int[] getSurfaceRows(BlockEnum[][] world) {
		int[] surfaceRows = new int[world[0].length];
		for (int c = 0; c < surfaceRows.length; c++) {
			for (int r = 0; r < WorldBuilder.WORLDHEIGHT; r++) {
				if (world[r][c] != null && world[r][c] != BlockEnum.WATER) {
					surfaceRows[c] = r;
				}
			}
		}
		return surfaceRows;
	}

	private void initSurfaceRows() {
		for (int c = 0; c < surfaceRows.length; c++) {
			for (int r = 0; r < WorldBuilder.WORLDHEIGHT; r++) {
				if (world[r][c] != null && world[r][c] != BlockEnum.WATER) {
					surfaceRows[c] = r;
				}
			}
		}
	}

	private void makeFishRock() {
		Collectable c = new Collectable(Collectable.FISHROCK);
		int randomCol = getRandomCol(c.getBiome());
		int row = surfaceRows[randomCol];
		c.setCol(randomCol); 
		c.setRow(row);
		collectables.add(c);
	}

	private void initCollectables() {
		ArrayList<Integer> generatedPositions = new ArrayList<Integer>();
		for (int type = 0; type < collectableList.length; type++) {
			int numToMake = collectableList[type].getNumToMake();
			for (int num = 0; num < numToMake; num++) {
				Collectable c = new Collectable(type);
				int randomCol = getRandomCol(c.getBiome());
				while(generatedPositions.contains(randomCol) || randomCol == xToCol(charX) || randomCol == xToCol(charX)+1) {
					randomCol = getRandomCol(c.getBiome());
				}
				int row = surfaceRows[randomCol];
				c.setCol(randomCol); 
				c.setRow(row);
				if(c.getType()!=Collectable.STICK&&c.getType()!=Collectable.PINEAPPLE&&(WorldBuilder.trees.contains(randomCol)||WorldBuilder.trees.contains(randomCol-1)||WorldBuilder.trees.contains(randomCol+1))) {
					boolean onTree = Math.random()<.7;
					if(onTree) {
						c.setRow(row+4+(int)(Math.random()+.5));
					}
					if(c.getType()==Collectable.STICK&&(WorldBuilder.trees.contains(randomCol-1)||WorldBuilder.trees.contains(randomCol+1))) {
						c.setRow(row+2+(int)(Math.random()+.5));
					}
				}
				generatedPositions.add(randomCol);
				collectables.add(c);
			}
		}
	}

	private void setNPCPos(NPC npc) {
		int biomeNum = npc.getBiome();
		int biomeIndex = WorldBuilder.biomeOrder.indexOf(biomeNum);
		int col = biomeIndex * WorldBuilder.BIOMEWIDTH + npc.getColInBiome();
		int row = surfaceRows[col];
		npc.setCol(col);
		npc.setRow(row);
	}

	private void initNPCs() {
		for (NPC n : NPCs) {
			setNPCPos(n);
			//System.out.println(n.getName() + " at " + n.getCol());
		}
	}

	private void talk(NPC npc) {
		Quest q = npc.getQuest();
		if (!npc.dialogueVisible()) {
			switch (npc) {
			case BATTHEW:	
				if (!q.started() || (!q.completed() && q.started() && !character.hasAllFruits())) {
					q.start();
					//if (!questList.contains(q)) { questList.add(q); };

					NPC.BATTHEW.setDialogueBlock(new DialogueBlock(new String[] {"i'm in a bit of a predicament", "i ate some spicy ramen",
							"and you probably can't tell", "but i'm dying inside", "help", "can you bring me some fruit",
							"specifically uhhhhh", "bananas mangoes and pineapples", "you can find them somewhere probably"}));
					q.setMessage("Bring 3 bananas, 3 mangoes, and 3 pineapples to Batthew");
					/*needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";*/
				}
				else if (!q.completed() && character.hasAllFruits()) {
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.BANANA, 3);
					character.reduceNum(Collectable.MANGO, 3);
					character.reduceNum(Collectable.PINEAPPLE, 3);
					character.addKey(WorldBuilder.GRASS);
					NPC.BATTHEW.setDialogueBlock(new DialogueBlock(new String[] {"big thanks here's the key™"}));
					needDrawNotif = true;
					notifMessage = "QUEST COMPLETE";
				}
				else if (q.completed()) {
					NPC.BATTHEW.setDialogueBlock(new DialogueBlock(new String[] {"gl with your other quests"}));
				}
				break;


			case SNOWMAN:
				if (!q.started() && !character.has(Equipable.COAT)) {
					NPC.SNOWMAN.setDialogueBlock(new DialogueBlock(new String[] {"oh gosh you look cold", "go find a coat so you", "don't freeze out here"}));
				}
				else if (!q.started() || (!q.completed() && q.started() && !character.hasAllSnowmanParts())) {
					q.start();
					//if (!questList.contains(q)) { questList.add(q); };
					NPC.SNOWMAN.setDialogueBlock(new DialogueBlock(new String[] {"boohoo boohoo", "some mean teenagers passed by",
							"and knocked my face and arms off", "boohoo boohoo", "could you find me some", "new eyes, arms, and a carrot nose?"}));
					q.setMessage("Bring 2 seashells, 2 sticks, and 1 carrot to Snowman");
					//needDrawNotif = true;
					//notifMessage = "NEW QUEST IN MENU";
				}
				else if (!q.completed() && character.hasAllSnowmanParts()) {
					NPC.SNOWMAN.setImage(snowmanDone);
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.SEASHELL, 2);
					character.reduceNum(Collectable.STICK, 2);
					character.reduceNum(Collectable.CARROT, 1);
					character.addKey(WorldBuilder.SNOW);
					NPC.SNOWMAN.setDialogueBlock(new DialogueBlock(new String[] {"woohoo woohoo", "thanks, here's the COOLEST key!"}));
					needDrawNotif = true;
					notifMessage = "QUEST COMPLETE";
				}
				else if (q.completed()) {
					NPC.SNOWMAN.setDialogueBlock(new DialogueBlock(new String[] {"Have fun adventuring!"}));
				}
				break;


			case FISH:
				if (!q.started() && !character.has(Equipable.GOGGLES)) {
					NPC.FISH.setDialogueBlock(new DialogueBlock(new String[] {"bro how are you keeping", "your eyes open underwater?", "go find goggles or it'll hurt bro"}));
				}
				else if (!q.started() || (!q.completed() && q.started() && !character.hasFishRock())) {
					q.start();
					//if (!questList.contains(q)) { questList.add(q); };
					NPC.FISH.setDialogueBlock(new DialogueBlock(new String[] {"blub blub", "hey bro, I'm in a bit of a pinch",
							"I got involved in some", "fishy business", "and I'm getting framed", "for a crime I didn't commit.",
							"the court will only let me go", "if I can bring some", "ROCK SOLID evidence", "that it wasn't me.", "if you see anything",
							"that you think might help my case,", "could you bring it to me?", "thanks bro."}));
					q.setMessage("Bring some ROCK SOLID evidence to Fish");
				}
				else if (character.hasFishRock()) {
					NPC.FISH.setDialogueBlock(new DialogueBlock(new String[] {"yoooooo dope", "thanks bro", "I don't have money for legal fees",
					"but here's some random key I found"}));
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.FISHROCK, 1);
					character.addKey(WorldBuilder.OCEAN);
					needDrawNotif = true;
					notifMessage = "QUEST COMPLETE";
				}
				else if (q.completed()) {
					NPC.FISH.setDialogueBlock(new DialogueBlock(new String[] {"stay lit"}));
				}
				break;


			case SNAKES:
				if (!q.completed()) {
					if (!q.started() && !character.has(Equipable.BUGSPRAY)) {
						NPC.SNAKES.setDialogueBlock(new DialogueBlock(new String[] {"thosssse bug bitessss", "don't look sssso good",
								"you sssshould conssssider", "invessssting in bugsssspray"}));
					}
					else if (!q.started()) {
						q.start();
						q.setMessage("Talk to the monkeys");
					}
					//if (!questList.contains(q)) { questList.add(q); };
					NPC.SNAKES.setDialogueBlock(new DialogueBlock(new String[] {"sssssssss we are angry", "we ordered a Python IDE key",
							"but got a Java one insssstead.", "Can you take our Java key", "to the monKeyssss", "who are key collectorssss,",
							"and ssssee if they'll", "trade for a Python one?"}));
					//needDrawNotif = true;
					//notifMessage = "NEW QUEST IN MENU";
				}
				else if (q.completed()) {
					NPC.SNAKES.setDialogueBlock(new DialogueBlock(new String[] {"Thankssss for the Python IDE key!"}));
				}
				break;


			case MONKEYS:
				if (!q.started()) {
					NPC.MONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"hello we are the monKeys,", "do you have any keys?"}));
				}
				else if (q.completed()) {
					NPC.MONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"Good luck with your key collecting!"}));
				}
				else if (q.started()) {
					boolean talkedToCollectors = (NPC.TURKEYS.talkedTo() && NPC.DOVEKEYS.talkedTo() && NPC.DONKEYS.talkedTo());
					if (!talkedToCollectors) {
						NPC.MONKEYS.setTalkedTrue();
						NPC.MONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"The snakes want a Python key", "AND you want the rainforest key?",
								"Hmmmmmmmmm...", "we'll give you both keys", "if you talk to our fellow", "key collector competitors,", "the doveKeys, donKeys, and turKeys,",
								"to make sure we still have", "the most keys total.", "Our collection has grown to", "a total of 365246 keys.", "Come back after you've", "talked to them all."}));
						q.setMessage("Talk to the doveKeys, donKeys, and turKeys and return to the monKeys");
					}
					else {
						NPC.MONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"We're still on top?", "Great!", "Here are your keys,", "hope to see you as", "a fellow key collector soon!"}));
						q.complete();
						questList.remove(q);
						character.addKey(WorldBuilder.RAINFOREST);
						needDrawNotif = true;
						notifMessage = "QUEST COMPLETE";
					}
				}
				break;


			case DOVEKEYS:
				if (!q.started() && !NPC.MONKEYS.talkedTo()) {
					NPC.DOVEKEYS.setDialogueBlock(new DialogueBlock(new String[] {"cheep cheep hello we are the doveKeys,", "do you have any keys?"}));
				}
				else if (NPC.MONKEYS.talkedTo() && !NPC.DOVEKEYS.talkedTo()) {
					NPC.DOVEKEYS.setTalkedTrue();
					NPC.DOVEKEYS.setDialogueBlock(new DialogueBlock(new String[] {"We just increased our collection", "to 5 keys total!", "Wait, the monKeys have",
							"HOW MANY??", ".....", "Alright, tell them that", "they definitely have the most."}));
				}
				else {
					NPC.DOVEKEYS.setDialogueBlock(new DialogueBlock(new String[] {"Good luck with your quests!"}));
				}
				break;


			case DONKEYS:
				if (!q.started() && !NPC.MONKEYS.talkedTo()) {
					NPC.DONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"neigh neigh hello we are the donKeys,", "do you have any keys?"}));
				}
				else if (NPC.MONKEYS.talkedTo() && !NPC.DONKEYS.talkedTo()) {
					NPC.DONKEYS.setTalkedTrue();
					NPC.DONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"The monKeys are just", "flexing on us by now.", "We only have 59 keys",
							"and they know it...", "Tell them that we'll", "be catching up soon though!"}));
				}
				else {
					NPC.DONKEYS.setDialogueBlock(new DialogueBlock(new String[] {"Good luck finding those keys!"}));
				}
				break;


			case TURKEYS:
				if (!q.started() && !NPC.MONKEYS.talkedTo()) {
					NPC.TURKEYS.setDialogueBlock(new DialogueBlock(new String[] {"gobble gobble hello we are the turKeys,", "do you have any keys"}));
				}
				else if (NPC.MONKEYS.talkedTo() && !NPC.TURKEYS.talkedTo()) {
					NPC.TURKEYS.setTalkedTrue();
					NPC.TURKEYS.setDialogueBlock(new DialogueBlock(new String[] {"We have -5 keys.", "We're in key debt", "to the monkeys...", "Don't ask how...",
							"Tell them please don't", "add interest, thanks..."}));
				}
				else {
					NPC.TURKEYS.setDialogueBlock(new DialogueBlock(new String[] {"Have fun with your adventure!"}));
				}
				break;


			case BUSH:
				if (!q.completed()) {
					if (!q.started() && !character.has(Equipable.BUGSPRAY)) {
						NPC.BUSH.setDialogueBlock(new DialogueBlock(new String[] {"Watch out, citizen,", "the sand is hot!", "Try to find some boots",
						"to protect your feet!"}));
					}
					else if (!q.started()) {
						q.start();
						//if (!questList.contains(q)) { questList.add(q); };
						NPC.BUSH.setDialogueBlock(new DialogueBlock(new String[] {"Hello, citizen!", "My name is Bush, and", "I'm running for mayor",
								"against my rival,", "the infamous local PRICK,", "Cactus.", "Could you find him and", "let him know that", "I definitely have more",
								"GRASSROOTS support", "and he'll really have to", "BRANCH out", "to more citizens", "if he wants to win?"}));
						q.setMessage("Talk to Cactus");
					}
					//needDrawNotif = true;
					//notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.BUSH.setDialogueBlock(new DialogueBlock(new String[] {"Vote Bush!"}));
				}
				break;


			case CACTUS:
				if (!q.started()) {
					NPC.CACTUS.setDialogueBlock(new DialogueBlock(new String[] {"sup I'm Cactus,", "vote for me for mayor lol"}));
				}
				else if (!q.completed()) {
					NPC.CACTUS.setDialogueBlock(new DialogueBlock(new String[] {"haha Bush is a funny dude", "tell him too bad that I'm", "SHARPER than him",
							"and he needs to get the", "POINT", "already and just concede", "remember,", "a vote for Cactus", "is a vote for the cactUS",
							"so vote Cactus 2k19", "here's the key as thanks", "for voting Cactus 2k19"}));
					q.complete();
					questList.remove(q);
					character.addKey(WorldBuilder.DESERT);
					needDrawNotif = true;
					notifMessage = "QUEST COMPLETE";
				}
				else {
					NPC.CACTUS.setDialogueBlock(new DialogueBlock(new String[] {"vote Cactus 2k19"}));
				}
				break;

			case CAT:
				if (!q.started() || (!q.completed() && q.started() && !character.has(1, Collectable.YARN))) {
					q.start();
					if (!questList.contains(q)) {
						questList.add(q);
						needDrawNotif = true;
						notifMessage = "NEW QUEST IN MENU";
					}
					NPC.CAT.setDialogueBlock(new DialogueBlock(new String[] {"meow hello", "I dropped my ball of yarn", "somewhere in town.", "If you see it,",
					"could you bring it to me?"}));
					q.setMessage("Bring Cat 1 ball of yarn");

				}
				else if (!q.completed() && character.has(1, Collectable.YARN)) {
					NPC.CAT.setDialogueBlock(new DialogueBlock(new String[] {"My yarn!", "Thank you!", "Here's a coat that I", "knitted from this yarn.",
							"Stay warm if you head", "into the snow biome!"}));
					character.addToInventory(Equipable.COAT);
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.YARN, 1);
					Quest.SNOWKEY.setMessage("Talk to Snowman");
					questList.add(Quest.SNOWKEY);
					needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.CAT.setDialogueBlock(new DialogueBlock(new String[] {"Good luck questing!"}));
				}
				break;


			case DOG:
				if (!q.started() || (!q.completed() && q.started() && !character.has(5, Collectable.BONE))) {
					q.start();
					if (!questList.contains(q)) {
						questList.add(q);
						needDrawNotif = true;
						notifMessage = "NEW QUEST IN MENU";
					}
					NPC.DOG.setDialogueBlock(new DialogueBlock(new String[] {"bork hi", "I dropped my tasty bones", "everywhere in town,",
					"could you help me collect them?"}));
					q.setMessage("Bring Dog 5 bones");
				}
				else if (!q.completed() && character.has(3, Collectable.BONE)) {
					NPC.DOG.setDialogueBlock(new DialogueBlock(new String[] {"My bones!", "Thanks!", "Here's some boots that I", "may have chewed on a bit",
							"but they'll still protect you", "from the hot sand", "if you go to the desert."}));
					character.addToInventory(Equipable.BOOTS);
					q.complete();
					character.reduceNum(Collectable.BONE, 5);
					questList.remove(q);
					Quest.DESERTKEY.setMessage("Talk to Cactus");
					questList.add(Quest.DESERTKEY);
					needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.DOG.setDialogueBlock(new DialogueBlock(new String[] {"Stay safe on your quests!"}));
				}
				break;


			case MOUSE:
				if (!q.started() || (!q.completed() && q.started() && !character.has(1, Collectable.CHEESE))) {
					q.start();
					if (!questList.contains(q)) {
						questList.add(q);
						needDrawNotif = true;
						notifMessage = "NEW QUEST IN MENU";
					}
					NPC.MOUSE.setDialogueBlock(new DialogueBlock(new String[] {"squeak", "I tried going vegan recently", "but I just can't do it",
							"could you get me some cheese", "please"}));
					q.setMessage("Bring Mouse 1 block of cheese");
				}
				else if (!q.completed() && character.has(1, Collectable.CHEESE)) {
					NPC.MOUSE.setDialogueBlock(new DialogueBlock(new String[] {"b l e s s", "thanks so much", "here's some bug spray", "that I use to protect my cheese",
							"it'll help with those", "pesky mosquitoes in the rainforest"}));
					character.addToInventory(Equipable.BUGSPRAY);
					q.complete();
					character.reduceNum(Collectable.CHEESE, 1);
					questList.remove(q);
					Quest.RAINFORESTKEY.setMessage("Talk to the snakes");
					questList.add(Quest.RAINFORESTKEY);
					needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.MOUSE.setDialogueBlock(new DialogueBlock(new String[] {"Come make 5-cheese lasagna with me sometime!"}));
				}
				break;


			case BIRD:
				if (!q.started() || (!q.completed() && q.started() && !character.has(1, Collectable.SUNFLOWER))) {
					q.start();
					if (!questList.contains(q)) {
						questList.add(q);
						needDrawNotif = true;
						notifMessage = "NEW QUEST IN MENU";
					}
					NPC.BIRD.setDialogueBlock(new DialogueBlock(new String[] {"cheep", "You know, I had this", "500 IQ idea the other day.", "Instead of buying those",
							"expensive packets of seeds,", "what if I just get", "a single sunflower", "for much CHEEPer", "but around the same number of seeds?",
							"If you see any sunflowers", "bring them to me", "I love free food"}));
					q.setMessage("Bring Bird 1 sunflower");
				}
				else if (!q.completed() && character.has(1, Collectable.SUNFLOWER)) {
					NPC.BIRD.setDialogueBlock(new DialogueBlock(new String[] {"Thanks, I'll be feasting tonight!", "Here's a pair of goggles", 
							"that I was using as", "aviator goggles until now.", "They'll help you see", "if you go for a swim", "in the ocean."}));
					character.addToInventory(Equipable.GOGGLES);
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.SUNFLOWER, 1);
					Quest.OCEANKEY.setMessage("Talk to Clownfish");
					questList.add(Quest.OCEANKEY);
					needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.BIRD.setDialogueBlock(new DialogueBlock(new String[] {"Come over for a snack sometime!"}));
				}
				break;


			case SQUIRREL:
				if (!q.started() || (!q.completed() && q.started() && !character.has(5, Collectable.ACORN))) {
					q.start();
					if (!questList.contains(q)) {
						questList.add(q);
						needDrawNotif = true;
						notifMessage = "NEW QUEST IN MENU";
					}
					NPC.SQUIRREL.setDialogueBlock(new DialogueBlock(new String[] {"A while ago I was", "carrying a mouthful of acorns", "to bring back home",
							"but I started listening to music", "and sung along", "so I dropped the acorns", "all over town...", "Could you pick them up",
					"and bring them to me?"}));
					q.setMessage("Bring Squirrel 5 acorns");
				}
				else if (!q.completed() && character.has(5, Collectable.ACORN)) {
					NPC.SQUIRREL.setDialogueBlock(new DialogueBlock(new String[] {"Thanks!", "Here's the flashlight", "that I was using when",
							"collecting acorns at night.", "It'll help if you want to", "check out that weird cave", "in the grasslands."}));
					character.addToInventory(Equipable.FLASHLIGHT);
					q.complete();
					questList.remove(q);
					character.reduceNum(Collectable.ACORN, 5);
					Quest.GRASSKEY.setMessage("Talk to Batthew");
					questList.add(Quest.GRASSKEY);
					needDrawNotif = true;
					notifMessage = "NEW QUEST IN MENU";
				}
				else {
					NPC.SQUIRREL.setDialogueBlock(new DialogueBlock(new String[] {"Come jam out with me sometime!"}));
				}
				break;


			default:
				break;
			}
			npc.getDialogueBlock().reset();
			npc.showDialogue();
		}
		else {
			npc.getDialogueBlock().nextLine();
		}
	}

	private BufferedImage[][] getRotatedWorld(BlockEnum[][] world) {
		BufferedImage[][] images = new BufferedImage[world.length][world[0].length];
		for (int r = 0; r < images.length; r++) {
			for (int c = 0; c < images[0].length; c++) {
				BlockEnum b = world[r][c];
				if (b != null) {
					if (b != BlockEnum.GRASS) {
						AffineTransform affine = new AffineTransform();
						BufferedImage image = b.getImage();
						int numRotations = (int)(Math.random() * 2);
						for (int i = 0; i < numRotations; i++) {
							//rotates 90 degrees around anchor point in center:
							affine.rotate(Math.PI / 2, image.getWidth() / 2, image.getHeight() / 2);
							//
							AffineTransformOp affineOp = new AffineTransformOp(affine, AffineTransformOp.TYPE_BILINEAR);
							image = affineOp.filter(image, null);
						}
						images[r][c] = image;
					}
					else {
						images[r][c] = b.getImage();
					}

				}
			}
		}
		return images;
	}

	private int rowToY(int blockRow) {
		return yPos - (blockRow * BLOCKSIZE);
	}

	private int colToX(int blockCol) {
		return xPos + (blockCol * BLOCKSIZE);
	}

	private int xToCol(int xPixel) {
		/*System.out.println((float)(xPixel - xPos));
		System.out.println((float)(BLOCKSIZE));*/
		//System.out.println("col " + Math.round((float)(xPixel - xPos) / (float)(BLOCKSIZE)));
		return Math.round((float)(xPixel - xPos) / (float)(BLOCKSIZE));
	}

	private int yToRow(int yPixel) {
		return Math.round((float)(yPos - yPixel) / (float)(BLOCKSIZE));
	}

	private int getBiomeFromPos(int xPos) {
		int index = (int) Math.floor((float)belowCol() / (float)WorldBuilder.BIOMEWIDTH);
		//System.out.println(index);
		int biome = WorldBuilder.biomeOrder.get(index);
		return biome;
	}

	private Rectangle makeRectangle(int blockRow, int blockCol) {
		return new Rectangle(colToX(blockCol), rowToY(blockRow), BLOCKSIZE, BLOCKSIZE);
	}

	private Rectangle makeRectangle(int blockRow, int blockCol, int width, int height) {
		return new Rectangle(colToX(blockCol), rowToY(blockRow), width, height);
	}

	private int[] belowRowCol() {
		int col = xToCol(charX);
		int row = yToRow(world.length - 1);
		while (world[row][col] == null || world[row][col] == BlockEnum.WATER) {
			row--;
		}
		int[] coords = {row, col};
		return coords;
	}

	private int belowRow() {
		return belowRowCol()[0];
	}

	private int belowCol() {
		return belowRowCol()[1];
	}

	private int belowY() {
		return rowToY(belowRow());
	}

	private int distanceBelow() {
		return ((charY + (BLOCKSIZE * 4) - belowY()));
	}

	private boolean collidesBelow() {
		belowRect = makeRectangle(belowRow(), belowCol());
		return belowRect.intersects(charRect);
	}

	private int getTicks() {
		return (int) ((System.currentTimeMillis() - initialTime) / MILLISPERTICK);	}

	private int getDayNightCyclePosition() {
		return (int)((Math.sin(((Math.PI/512f)*((getTicks())%1024)-(512)) - Math.PI/1.5d)/2 + .5f )*512f);	}
	private Color getSkyColor() {
		//System.out.println("day/night " + getDayNightCyclePosition());

		int r = (int) (getDayNightCyclePosition() * ((126f)/512f)) ;
		int g = (int) (getDayNightCyclePosition() * ((201f)/512f));
		int b = (int) (getDayNightCyclePosition() * ((252f)/512f));
		//System.out.println(g);
		//System.out.println(b);

		Color skyColor = new Color(r,g,b);
		return skyColor;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(getSkyColor());
		drawParticles(g);
		drawWorld(g, images);
		//System.out.println("after draw");
		drawTrees(g);
		drawTown(g);
		drawCollectables(g);
		drawNPCs(g);
		//drawInventory(g);
		if (needDrawNotif) {
			drawNotif(g);
		}
		if (needDrawOuch) {
			drawOuch(g, getBiomeFromPos(xPos));
		}
		drawChar(g);
		if (drawArrows) {
			drawArrows(g);
		}
		if (needDrawMosquito) {
			drawMosquito(g);
		}
		if (!menuOpen) {
			drawInterface(g);
		}
		drawHealthBar(g);
		fadeScreen(g);

		//g.drawRect(charRect.x, charRect.y, charRect.width, charRect.height);
		//g.drawRect(belowX(), belowY(), BLOCKSIZE, BLOCKSIZE);
		if (menuOpen) {
			drawMenu(g);
		}

	}

	private void fadeScreen(Graphics g) {
		if(fade!=0) {
			g.setColor(new Color(0,0,0,fade));
			g.fillRect(0,0,SCREENWIDTH,SCREENHEIGHT);
		}
	}

	private void drawParticles(Graphics g) {
		for(int i=0; i<particles.size(); i++) {
			Particle currentParticle = particles.get(i);
			if (currentParticle.getType()==Particle.SNOW) {

				g.setColor(Color.WHITE);
				g.drawOval(currentParticle.getX(), currentParticle.getY(), 2,2);
			}
			else if(currentParticle.getType()==Particle.SAND) {
				g.setColor(Color.YELLOW);
				g.drawRect(currentParticle.getX(), currentParticle.getY(), 2, 2);
			}
			else if(currentParticle.getType()==Particle.RAIN) {
				g.setColor(rainColor);
				g.fillRect(currentParticle.getX(), currentParticle.getY(), 4, 8);
			}
			if(currentParticle.getY()>this.getHeight()) {
				particles.remove(i);
			}
		}
	}

	private void drawMosquito(Graphics g) {
		Image mosquito = new ImageIcon(Main.class.getResource("/bugs.gif")).getImage();
		g.drawImage(mosquito, charX, charY, BLOCKSIZE*2, BLOCKSIZE*2, this);
	}

	private void drawArrows(Graphics g) {
		g.drawImage(pressLeftImage, charX - 2 * BLOCKSIZE, charY + BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
		g.drawImage(pressRightImage, charX + 3 * BLOCKSIZE, charY + BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
		g.drawImage(pressUpImage, charX + HALFBLOCKSIZE, charY - BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
	}

	private void drawInterface(Graphics g) {
		g.setFont(titleFont);
		String string = "to open menu";
		int stringWidth = g.getFontMetrics().stringWidth(string);
		int stringX = SCREENWIDTH - stringWidth - HALFBLOCKSIZE;
		int stringY = BLOCKSIZE;

		g.setColor(new Color(255, 255, 255, 50));
		g.fillRect(stringX - BLOCKSIZE*2, 0, stringWidth + 3*BLOCKSIZE, BLOCKSIZE*2);

		g.setColor(Color.WHITE);
		g.drawString(string, stringX, stringY);
		g.drawImage(pressMImage, stringX - BLOCKSIZE - HALFBLOCKSIZE, (int)((float)BLOCKSIZE / 4f), BLOCKSIZE, BLOCKSIZE, this);
	}

	private void drawHealthBar(Graphics g) {
		double healthBarWidth = (double)SCREENWIDTH / 5d;
		int healthBarHeight = (int)((double)(SCREENHEIGHT / 100d));
		g.setColor(Color.RED);
		g.fillRect(0, 0, (int)(character.getHealth()/100 * healthBarWidth), healthBarHeight);
		g.setColor(Color.GRAY);
		g.fillRect((int)(int)(character.getHealth()/100 * healthBarWidth), 0, (int)(healthBarWidth- (character.getHealth()/100 * healthBarWidth)), healthBarHeight);
		g.setColor(Color.WHITE);
		g.setFont(titleFont);
		g.drawString("HP", (int)((float)BLOCKSIZE / 4f), (int)(3f * (float)BLOCKSIZE / 4f));
	}

	private void drawWorld(Graphics g, BufferedImage[][] images) {
		for (int r = 0; r < world.length; r++) {
			for (int c = 0; c < world[0].length; c++) {
				int blockX = xPos + c * BLOCKSIZE;
				int blockY = yPos - r * BLOCKSIZE;
				if (blockX > -BLOCKSIZE && blockX < SCREENWIDTH + BLOCKSIZE
						&& blockY > -BLOCKSIZE && blockY < SCREENHEIGHT + BLOCKSIZE) {
					drawBlock(g, images[r][c], blockX, blockY);
				}
			}
		}
		g.setColor(Color.GRAY);
		g.fillRect(xPos - (SCREENWIDTH / 2), yPos, (SCREENWIDTH / 2), 1000);
	}

	private void drawWorld(Graphics g, BlockEnum[][] world) {
		for (int r = 0; r < world.length; r++) {
			for (int c = 0; c < world[0].length; c++) {
				int blockX = xPos + c * BLOCKSIZE;
				int blockY = yPos - r * BLOCKSIZE;
				if (blockX > -BLOCKSIZE && blockX < SCREENWIDTH + BLOCKSIZE
						&& blockY > -BLOCKSIZE && blockY < SCREENHEIGHT + BLOCKSIZE) {
					drawBlock(g, world[r][c], blockX, blockY);
				}
			}
		}
	}

	private void drawChar(Graphics g) {
		g.drawImage(character.getImage(), charX, charY, BLOCKSIZE * 2, BLOCKSIZE * 4, this);
	}

	private void drawArrow(Graphics g, int x, int y) {
		g.drawImage(arrowImage, x, y, BLOCKSIZE, BLOCKSIZE, this);
	}

	private void drawArrow(Graphics g, NPC n) {
		g.drawImage(arrowImage, colToX(n.getCol()) + (int)((float)n.getWidth() / 2f) - (int)((float)BLOCKSIZE / 2f), rowToY(n.getRow()) - n.getHeight() - BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
	}

	private void drawBlock(Graphics g, BufferedImage b, int x, int y) {
		if (b != null) {
			g.drawImage(b, x, y, BLOCKSIZE, BLOCKSIZE, this);
		}
	}

	private void drawBlock(Graphics g, BlockEnum b, int x, int y) {
		if (b != null) {
			BufferedImage image = b.getImage();
			g.drawImage(image, x, y, BLOCKSIZE, BLOCKSIZE, this);
		}
	}

	private void drawCollectable(Graphics g, Collectable c) {
		int size = (int)((float)BLOCKSIZE * 1.5f);
		g.drawImage(c.getImage(), colToX(c.getCol()), rowToY(c.getRow()) - size, size, size, this);

	}

	private void drawCollectables(Graphics g) {
		for (int i = 0; i < collectables.size(); i++) {
			Collectable c = collectables.get(i);
			int col = c.getCol();
			int x = colToX(col);
			int y = rowToY(c.getRow());
			if (x > -BLOCKSIZE && x < SCREENWIDTH + BLOCKSIZE
					&& y > -BLOCKSIZE && y < SCREENHEIGHT + BLOCKSIZE) {
				drawCollectable(g, c);
				if (col == xToCol(charX) || col == xToCol(charX) + 1) {
					drawArrow(g, x, y - (5 * BLOCKSIZE));
				}
			}
		}
	}

	private void drawOuch(Graphics g, int biome) {
		//System.out.println("ouch");
		String message = "ouch oof";
		switch (biome) {
		case WorldBuilder.DESERT:
			message += " the sand is too hot to walk on!";
			break;
		case WorldBuilder.OCEAN:
			message += " the water is getting in your eyes!";
			break;
		case WorldBuilder.SNOW:
			message += " it's really cold!";
			break;
		case WorldBuilder.RAINFOREST:
			message += " the mosquitoes are brutal";
			break;
		default:
			break;
		}
		g.drawImage(warning, HALFBLOCKSIZE, BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
		g.setFont(titleFont);
		int stringX = 2 * BLOCKSIZE;
		int stringY = BLOCKSIZE + HALFBLOCKSIZE;
		g.setColor(Color.WHITE);
		g.drawString(message, stringX, stringY + (int)((float)BLOCKSIZE / 10f));
	}

	private void drawNotif(Graphics g) {
		int stringWidth = getFontMetrics(titleFont).stringWidth(notifMessage);
		int stringHeight = getFontMetrics(titleFont).getHeight();
		int stringX = (int)((float)SCREENWIDTH / 2f);
		int stringY = BLOCKSIZE * 2;
		g.setColor(new Color(255, 255, 255, 50));
		g.fillRect(stringX - HALFBLOCKSIZE, stringY - BLOCKSIZE, stringWidth + BLOCKSIZE, stringHeight * 2);
		g.setColor(Color.WHITE);
		g.setFont(titleFont);
		g.drawString(notifMessage, stringX, stringY);
	}

	private void drawMenu(Graphics g) {
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRect(BLOCKSIZE, BLOCKSIZE, SCREENWIDTH - (2 * BLOCKSIZE), SCREENHEIGHT - (4 * BLOCKSIZE));
		drawInventory(g);
		drawQuests(g);
		drawEquipables(g);
		drawKeys(g);
		g.drawImage(pressMImage, SCREENWIDTH - 6*BLOCKSIZE, SCREENHEIGHT - 5*BLOCKSIZE + (int)((float)HALFBLOCKSIZE/2f), BLOCKSIZE, BLOCKSIZE, this);
		g.drawString("to close menu", SCREENWIDTH - 5*BLOCKSIZE + HALFBLOCKSIZE, SCREENHEIGHT - 4*BLOCKSIZE);

	}

	private void drawInventory(Graphics g) {
		g.setColor(Color.BLACK);
		int inventoryX = 2 * BLOCKSIZE;
		int inventoryY = 2 * BLOCKSIZE;
		g.setFont(titleFont);
		g.drawString("INVENTORY", inventoryX, inventoryY);
		for (int i = 0; i < collectableList.length; i++) {
			Collectable c = collectableList[i];
			int x = inventoryX + (int)((float)BLOCKSIZE/2);
			int y = inventoryY + (int)((float)(BLOCKSIZE) * 1.2f * (i+0.5));
			g.drawImage(c.getCenteredImage(), x, y, BLOCKSIZE, BLOCKSIZE, this);
			g.drawRect(x, y, BLOCKSIZE, BLOCKSIZE);
			//System.out.println(character.numOf(c) + " of " + c.getType());
			g.drawString("x " + character.numOf(c), x + (int)((float)BLOCKSIZE * 1.4f), y + (int)((float)BLOCKSIZE / 1.4f));
		}
	}

	private void drawQuests(Graphics g) {
		int textX = BLOCKSIZE * 8;
		int textY = 2 * BLOCKSIZE;
		g.setColor(Color.BLACK);
		g.setFont(titleFont);
		g.drawString("QUESTS", textX, textY);
		g.setFont(normalFont);
		for (int i = 0; i < questList.size(); i++) {
			Quest q = questList.get(i);
			g.drawString(q.getName() + " - " + q.getMessage(), textX, textY + (BLOCKSIZE * (i+1))); 
		}
	}

	private void drawEquipables(Graphics g) {
		int textX = BLOCKSIZE * 8;
		int textY = 14 * BLOCKSIZE;
		g.setColor(Color.BLACK);
		g.setFont(titleFont);
		g.drawString("ITEMS", textX, textY);
		g.setFont(normalFont);
		for (int i = 0; i < Equipable.ALLEQUIPABLES.length; i++) {
			Equipable e = Equipable.getEquipable(i);
			if (character.has(e)) {
				g.drawImage(e.getImage(), textX + ((3*BLOCKSIZE)*i), textY + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE, this);
				g.drawImage(numImages[i], textX + ((3*BLOCKSIZE)*i), textY + 3*BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, this);
				if (character.getCurrentItem() == e) {
					g.setColor(Color.RED);
				}
				else {
					g.setColor(Color.BLACK);
				}
				Graphics2D g2D = (Graphics2D)(g);
				g2D.setStroke(new BasicStroke(5));
				g2D.drawRect(textX + (3*BLOCKSIZE*i), textY + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE);
			}
			else {
				Image questionMark = new ImageIcon(Main.class.getResource("/unknown.png")).getImage();
				g.drawImage(questionMark, textX + ((3*BLOCKSIZE)*i), textY + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE, this);
			}
		}
	}

	private void drawKeys(Graphics g) {
		g.setFont(titleFont);
		int stringWidth = g.getFontMetrics().stringWidth("KEYS");
		int textX = SCREENWIDTH - 4*BLOCKSIZE - stringWidth;
		int textY = 2 * BLOCKSIZE;
		g.setColor(Color.BLACK);

		g.drawString("KEYS", textX, textY);
		g.setFont(normalFont);
		for (int i = 0; i < 5; i++) {
			Graphics2D g2D = (Graphics2D)(g);
			g2D.setStroke(new BasicStroke(5));
			g2D.drawRect(textX, textY + (3*BLOCKSIZE*i) + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE);
			if (character.hasKey(i)) {
				g.drawImage(keyImages[i], textX, textY + (3*BLOCKSIZE*i) + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE, this);
			}
			else {
				Image questionMark = new ImageIcon(Main.class.getResource("/unknown.png")).getImage();
				g.drawImage(questionMark,textX, textY + (3*BLOCKSIZE*i) + BLOCKSIZE, 2*BLOCKSIZE, 2*BLOCKSIZE, this);

			}
		}
	}

	private void drawTown(Graphics g) {
		int townIndex = WorldBuilder.biomeOrder.indexOf(WorldBuilder.TOWN);
		int leftCol = townIndex * WorldBuilder.BIOMEWIDTH;
		int x = colToX(leftCol);
		int y = rowToY(surfaceRows[leftCol]) - (10 * BLOCKSIZE);
		//System.out.println(x + " " + y);
		g.drawImage(townImage, x, y, WorldBuilder.BIOMEWIDTH * BLOCKSIZE, 10 * BLOCKSIZE, this);

		chestX = x + (BLOCKSIZE * WorldBuilder.BIOMEWIDTH / 2) - (int)((float)BLOCKSIZE * 3.75f);
		chestY = y + (7 * BLOCKSIZE);
		g.drawImage(chestImage, chestX, chestY, BLOCKSIZE * 2, BLOCKSIZE * 2, this);
		chestRect = new Rectangle(chestX, chestY, BLOCKSIZE * 2, BLOCKSIZE * 2);
		if (chestRect.intersects(charRect)) {
			if (!character.hasAllKeys()) {
				g.setColor(Color.WHITE);
				g.setFont(dialogueFont);
				String string = "A mysterious chest with five keyholes...maybe you can find 5 keys?";
				int stringWidth = g.getFontMetrics().stringWidth(string);
				g.drawString(string, chestX + BLOCKSIZE - (int)((float)stringWidth/2f), chestY - (2 * BLOCKSIZE));
			}
			else {
				frame.invalidate();
				frame.removeKeyListener(this);
				Ending e = new Ending(frame);
			}
		}
		//g.drawRect(chestRect.x, chestRect.y, chestRect.width, chestRect.height);
	}

	private int getRandomCol(int biomeNum) {
		int biomeIndex = WorldBuilder.biomeOrder.indexOf(biomeNum);
		int col = biomeIndex * WorldBuilder.BIOMEWIDTH + (int)(Math.random() * (float)WorldBuilder.BIOMEWIDTH);
		return col;
	}

	private void makeTrees() {
		treePositions = new int[WorldBuilder.trees.size()][2];
		for(int i=0; i<WorldBuilder.trees.size(); i++) {
			int col = WorldBuilder.trees.get(i);
			treePositions[i][0] = surfaceRows[col];
			treePositions[i][1] = col;
		}
	}

	private void drawTrees(Graphics g) {
		for (int i = 0; i < treePositions.length; i++) {
			drawTree(g, treePositions[i][0], treePositions[i][1]);
		}
	}

	private void drawTree(Graphics g, int bottomRow, int middleCol) {
		int leftCol = middleCol - 1;
		for (int r = 0; r < tree.length; r++) {
			for (int c = 0; c < tree[0].length; c++) {
				int drawRow = bottomRow + tree.length - r;
				int drawCol = leftCol + c;
				drawBlock(g, tree[r][c], colToX(drawCol), rowToY(drawRow));
			}
		}
	}

	private void drawNPCs(Graphics g) {
		for (NPC n : NPCs) {
			if (n != null) {
				drawNPC(g, n);
				if (n.getRect() != null && charRect.intersects(n.getRect()) && n.isInteractable()) {
					drawArrow(g, n);
				}
				else {
					n.hideDialogue();
				}
				if (n.dialogueVisible()) {
					drawDialogue(g, n);
				}
			}
		}
	}

	private void drawNPC(Graphics g, NPC npc) {
		//System.out.println("npc at col " + npc.getCol());
		int x = colToX(npc.getCol());
		int y = rowToY(npc.getRow()) - npc.getHeight();
		if (x > -npc.getWidth() && x < SCREENWIDTH + npc.getWidth()
		&& y > -BLOCKSIZE && y < SCREENHEIGHT + BLOCKSIZE) {
			g.drawImage(npc.getImage(), x, y, npc.getWidth(), npc.getHeight(), this);
			g.setColor(Color.ORANGE);
			int rectWidth = 4 * BLOCKSIZE;
			npc.setRect(makeRectangle(npc.getRow() + (npc.getHeight() / BLOCKSIZE), npc.getCol() - ((rectWidth / BLOCKSIZE) / 2), rectWidth + npc.getWidth(), npc.getHeight()));
			//g.drawRect(npc.getRect().x, npc.getRect().y, npc.getRect().width, npc.getRect().height);
		}
	}

	private void drawDialogue(Graphics g, NPC npc) {
		//System.out.println("draw " + npc + " dialogue " + npc.getDialogueBlock());
		String line = npc.getDialogueBlock().getCurrentLine();
		if (line != null) {
			g.setFont(dialogueFont);
			float stringLength = g.getFontMetrics().stringWidth(line);
			int centeredX = colToX(npc.getCol()) + (int)((float)npc.getWidth() / 2f) - HALFBLOCKSIZE;
			int stringX = centeredX - (int)(stringLength / 2f);
			int stringY = rowToY(npc.getRow()) - npc.getHeight() - (2 * BLOCKSIZE);
			int stringHeight = g.getFontMetrics().getHeight();
			g.setColor(Color.WHITE);
			g.fillRoundRect(stringX - HALFBLOCKSIZE, stringY - stringHeight, (int)stringLength + BLOCKSIZE, stringHeight * 2, HALFBLOCKSIZE, HALFBLOCKSIZE);
			g.setColor(Color.BLACK);
			g.setFont(dialogueFont);
			g.drawString(line, stringX, stringY);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!keysPressed.contains(keyCode)) {
			keysPressed.add(keyCode);
			//System.out.println("adding " + keyCode);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		keysPressed.remove(new Integer(keyCode));
		//System.out.println("removing " + keyCode);
		if (inputEnabled) {
			//letter M
			if (keyCode == 77 && !menuOpen) {
				menuOpen = true;
			}
			else if (keyCode == 77 && menuOpen) {
				menuOpen = false;
			}

			//numbers
			char keyChar = e.getKeyChar();
			if (menuOpen && java.lang.Character.isDigit(keyChar)) {
				String charString = "" + keyChar;
				int numPressed = Integer.parseInt(charString);
				if (numPressed >=1 && numPressed <= Equipable.ALLEQUIPABLES.length) {
					//System.out.println("number pressed: " + numPressed);
					Equipable equip = Equipable.getEquipable(numPressed-1);
					if (character.has(equip)) {
						character.setEquippedItem(equip);
					}
				}
			}
		}
		if(inputEnabled && !menuOpen) {
			//down
			if (keyCode == 40) {
				NPC speaker = null;
				for (int i = 0; i < NPCs.length; i++) {
					NPC n = NPCs[i];
					if (n != null && n.getRect() != null && charRect.intersects(n.getRect()) && n.isInteractable()) {
						speaker = n;
					}
				}
				if (speaker != null) {
					talk(speaker);
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void setCharImage(String direction, String action) {
		character.setImage(direction, action);
	}
	public void actionPerformed(ActionEvent e) {
		if (needDrawNotif) {
			notifTime++;
		}
		else {
			notifTime = 0;
		}
		if (notifTime >= 100) {
			needDrawNotif = false;
		}
		//System.out.println("world at " + xToCol(xPos));
		if (loopCycles>=0 && loopCycles<51) {
			if(loopCycles==0) {
				character.setImage("", "stand");
			}
			fade-=5;
			inputEnabled=false;
		}
		else {
			fade=0;
			inputEnabled=true;
			drawArrows = !moved;
		}
		for(int i=0; i<particles.size(); i++) {
			particles.get(i).update();
		}

		needDrawOuch = false;
		needDrawMosquito = false;

		if(WorldBuilder.DESERT==this.getBiomeFromPos(xPos)) {
			if (!(character.getCurrentItem() == Equipable.BOOTS)) {
				character.setHealth(character.getHealth()-.4);
				needDrawOuch = true;
			}
			if(loopCycles%3==0) {
				particles.add(new Particle(Particle.SAND,(int)(Math.random()*(double)(this.getWidth())), 6, 10));
			}
		}
		else if(WorldBuilder.SNOW==this.getBiomeFromPos(xPos)) {
			if (!(character.getCurrentItem() == Equipable.COAT)) {
				character.setHealth(character.getHealth()-.4);
				needDrawOuch = true;
			}
			if(loopCycles%10==0) {
				double snowVel = (2*Math.random()/3d + 1);
				particles.add(new Particle(Particle.SNOW,(int)(Math.random()*(double)(this.getWidth())), (int)(1.5d*snowVel), (int)(4d*snowVel)));
			}
		}
		else if(WorldBuilder.RAINFOREST==this.getBiomeFromPos(xPos)) {
			if (!(character.getCurrentItem() == Equipable.BUGSPRAY)) {
				character.setHealth(character.getHealth()-.4);
				needDrawOuch = true;
				needDrawMosquito = true;
			}
			if(loopCycles%2==0) {
				particles.add(new Particle(Particle.RAIN,(int)(Math.random()*(double)(this.getWidth())), 2, 16));			}
		}
		else if(WorldBuilder.OCEAN==this.getBiomeFromPos(xPos) && world[yToRow(charY)][xToCol(charX)] == BlockEnum.WATER) {
			if (!(character.getCurrentItem() == Equipable.GOGGLES)) {
				character.setHealth(character.getHealth()-.4);
				needDrawOuch = true;
			}
		}
		else if (WorldBuilder.GRASS==this.getBiomeFromPos(xPos)) {
			if (NPC.BATTHEW.getRect() == null || character.getCurrentItem() != Equipable.FLASHLIGHT) {
				NPC.BATTHEW.setImage(battClosed);
				NPC.BATTHEW.setInteractable(false);
			}
			else if (NPC.BATTHEW.getRect() != null && NPC.BATTHEW.getRect().intersects(charRect)) {
				NPC.BATTHEW.setImage(battOpen);
				NPC.BATTHEW.setInteractable(true);
			}
		}

		if (character.getHealth() < 100) {
			character.setHealth(character.getHealth()+.2);
		}

		if (character.getHealth() < 0) {
			xPos = xStart;
			yPos = yStart;
			yPos+=distanceBelow();
			yVel=0;
			xVel=0;
			character.setHealth(100);
			initialTime = System.currentTimeMillis();
			fade=255;
			loopCycles=-1;
			//System.exit(0);
		}

		if(inputEnabled && !menuOpen) {
			//right
			if (keysPressed.size() > 0) {
				moved = true;
			}

			if (keysPressed.contains(39)) {
				int worldWidth = WorldBuilder.BIOMEWIDTH * 6 * BLOCKSIZE;
				int rightXPos = xPos + worldWidth;
				int charRightXPos = charX + BLOCKSIZE;
				if (rightXPos - charRightXPos > MOVEINCREMENT) {
					xVel = -MOVEINCREMENT;
				}
				else {
					xPos = charRightXPos - worldWidth;
					xVel = 0;
				}
			}

			//left
			else if (keysPressed.contains(37)) {
				if (charX - xPos > MOVEINCREMENT) {
					xVel = MOVEINCREMENT;
				}
				else {
					xPos = charX;
					xVel = 0;		
				}
			}
			else {
				if(xVel >0) {
					if(xVel < WALK_DEACCEL) {
						xVel=0; 				}
					else {
						xVel -= WALK_DEACCEL;
					}

				}
				else if (xVel<0){
					if(xVel > -WALK_DEACCEL) {
						xVel=0; 
					}
					else {
						xVel += WALK_DEACCEL;
					}	
				}
			}

			//up 
			if (keysPressed.contains(38) && collidesBelow()) {
				yVel = JUMPVEL;
			}
		}
		else {
			xVel=0;
			yVel=0;
		}
		//yPos positive makes character go up relative to world
		//xPos positive makes character go left relative to world
		xPos += xVel;
		yPos += yVel;
		if (!collidesBelow()) {
			yVel -= GRAVITY;
		}
		else {
			yVel = 0;
			yPos += distanceBelow() - 1;
		}

		//System.out.println("y velocity: " + yVel);

		//down
		if (keysPressed.contains(40)) {
			Iterator<Collectable> itr = collectables.iterator();
			while (itr.hasNext()) {
				Collectable c = (Collectable)itr.next();
				int charCol = xToCol(charX);
				if (c.getCol() == charCol || c.getCol() == (charCol + 1)) {
					character.addToInventory(c);
					//System.out.println("add " + c);
					itr.remove();
				}
			}
		}

		if(xVel>0) {
			charDirection="L";
		}
		else if(xVel<0) {
			charDirection="R";
		}
		else {
			charDirection="";
		}

		if(collidesBelow()) {
			if(charDirection.equals("")) {
				character.setImage(charDirection, "stand");
			}
			else {
				character.setImage(charDirection, "run");
			}
		}
		else {
			if(charDirection.equals("")) {
				character.setImage(charDirection, "stand");
			}
			else {
				character.setImage(charDirection, "jump");
			}
		}

		this.setBackground(getSkyColor());	
		repaint();
		loopCycles++;
	}
}