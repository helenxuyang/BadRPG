import java.util.ArrayList;
import java.util.Collections;

public class WorldBuilder {
	public final static int WORLDHEIGHT = 100;
	public final static int BIOMEWIDTH = 250;
	public final static int WORLDWIDTH = BIOMEWIDTH * 6;
	public static ArrayList<Integer> biomeOrder;
	public static final int SNOW = 0;
	public static final int DESERT = 1;
	public static final int GRASS = 2;
	public static final int RAINFOREST = 3;
	public static final int OCEAN = 4;
	public static final int TOWN = 5;
	private static boolean regenRequired = false;
	public static ArrayList<Integer> trees;

	private static BlockEnum[][] genSnow(int width, int startHeight, int endHeight) {
		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			int dirtHeight = startHeight;
			for (int j = 0; j < dirtHeight; j++) {
				world[j][i] = BlockEnum.DIRT;
			}
		}
		double wave1PhaseShift = Math.random() * 2 - 1;
		double wave2PhaseShift = Math.random() - .5;
		double wave3PhaseShift = Math.random();
		double direction = Math.random();
		int offset = 0;

		for (int i = 0; i < width; i++) {
			double height = (Math.sin(i / 30d + wave1PhaseShift) * 10d);
			if (direction > .5) {
				height = -height;
			}
			double height2 = height + (Math.sin(i / 20 + wave2PhaseShift) * 2);
			double height3 = height + (Math.sin(i / 10d + wave3PhaseShift) * 1);

			if (i == 0) {
				offset = (int) height3;
			}
			height3 -= offset;
			if (height3 > 0) {
				for (int j = 0; j < height3 || j < 2; j++) {
					if (height3 - j > 2) {
						world[j + startHeight][i] = BlockEnum.DIRT;
					} else {
						world[j + startHeight][i] = BlockEnum.SNOW;

					}
				}
			} else {
				for (int j = 0; j > height3 || j > -2; j--) {
					if (j - height3 > 2) {
						world[j + startHeight][i] = null;
					} else {
						world[j + startHeight][i] = BlockEnum.SNOW;

					}
				}
			}
		}
		return world;
	}

	private static BlockEnum[][] genDesert(int width, int startHeight, int endHeight) {
		int offset = 0;

		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < startHeight; j++) {
				world[j][i] = BlockEnum.SAND;
			}
		}

		double wave1PhaseShift = Math.random() * 2 - 1;
		double wave2PhaseShift = Math.random() - .5;
		double direction = Math.random();

		for (int i = 0; i < width; i++) {
			double height = (Math.sin(i / 30d) * 5 + wave1PhaseShift) + 4;
			if (direction > .5) {
				height = -height;
			}
			double height2 = height + (Math.sin(i / 20f + wave2PhaseShift) * 2);

			if (i == 0) {
				offset = (int) height2;
			}
			if (NPC.DONKEYS.getColInBiome() <= i && NPC.DONKEYS.getColInBiome() + 6 >= i) {
				height = (Math.sin(NPC.DONKEYS.getColInBiome() / 30d) * 5 + wave1PhaseShift) + 4;
				if (direction > .5) {
					height = -height;
				}
				height2 = + (Math.sin(NPC.DONKEYS.getColInBiome() / 20f + wave2PhaseShift) * 2);
				height2 -= offset;
				if (height2 > 0) {
					for (int j = 0; j < height2; j++) {
						world[j + startHeight][i] = BlockEnum.SAND;
					}
				} else {
					for (int j = 0; j > height2; j--) {
						world[j + startHeight][i] = null;
					}
				}

			} else {
				height2 -= offset;
				if (height2 > 0) {
					for (int j = 0; j < height2; j++) {
						world[j + startHeight][i] = BlockEnum.SAND;
					}
				} else {
					for (int j = 0; j > height2; j--) {
						world[j + startHeight][i] = null;
					}
				}
			}

		}
		return world;
	}

	private static BlockEnum[][] genRainforest(int width, int startHeight, int endHeight) {
		int offset = 0;

		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < startHeight; j++) {
				world[j][i] = BlockEnum.DIRT;
			}
		}

		double wave1PhaseShift = Math.random() * 2 - 1;
		double wave2PhaseShift = Math.random() - .5;
		double direction = Math.random();
		for (int i = 0; i < width; i++) {
			double height = (Math.sin(i / 35f + wave1PhaseShift) * 6) + 4;
			if (direction > .5) {
				height = -height;
			}
			double height2 = height + (Math.sin(i / 20f + wave2PhaseShift) * 3);

			if (i == 0) {
				offset = (int) height2;
			}

			if (NPC.MONKEYS.getColInBiome() <= i && NPC.MONKEYS.getColInBiome() + 3 >= i) {
				height = (Math.sin(NPC.MONKEYS.getColInBiome() / 35f + wave1PhaseShift) * 6) + 4;
				if (direction > .5) {
					height = -height;
				}
				height2 = + (Math.sin(NPC.MONKEYS.getColInBiome() / 20f + wave2PhaseShift) * 3);
				height2 -= offset;
				if (height2 > 0) {
					for (int j = 0; j < height2 || j < 2; j++) {
						if (height2 - j > 2) {
							world[j + startHeight][i] = BlockEnum.DIRT;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				} else {
					for (int j = 0; j > height2 || j > -2; j--) {
						if (j - height2 > 2) {
							world[j + startHeight][i] = null;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				}

			} else if (NPC.SNAKES.getColInBiome() <= i && NPC.SNAKES.getColInBiome() + 3 >= i) {
				height = (Math.sin(NPC.SNAKES.getColInBiome() / 35f + wave1PhaseShift) * 6) + 4;
				if (direction > .5) {
					height = -height;
				}
				height2 = height + (Math.sin(NPC.SNAKES.getColInBiome() / 20f + wave2PhaseShift) * 3);
				height2 -= offset;
				if (height2 > 0) {
					for (int j = 0; j < height2 || j < 2; j++) {
						if (height2 - j > 2) {
							world[j + startHeight][i] = BlockEnum.DIRT;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				} else {
					for (int j = 0; j > height2 || j > -2; j--) {
						if (j - height2 > 2) {
							world[j + startHeight][i] = null;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				}

			} else {
				height2 -= offset;
				if (height2 > 0) {
					for (int j = 0; j < height2 || j < 2; j++) {
						if (height2 - j > 2) {
							world[j + startHeight][i] = BlockEnum.DIRT;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				} else {
					for (int j = 0; j > height2 || j > -2; j--) {
						if (j - height2 > 2) {
							world[j + startHeight][i] = null;
						} else {
							world[j + startHeight][i] = BlockEnum.GRASS;
						}
					}
				}
			}
		}

		return world;
	}

	private static BlockEnum[][] genGrass(int width, int startHeight, int endHeight) {
		int offset = 0;

		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < startHeight; j++) {
				world[j][i] = BlockEnum.DIRT;
			}
		}

		double wave1PhaseShift = Math.random() * 2 - 1;
		double wave2PhaseShift = Math.random() - .5;
		double direction = Math.random();

		//--------------------------------------------
		int i = 0;
		double height = 0;
		double height2 = 0;
		for (; i < NPC.BATTHEW.getColInBiome(); i++) {
			height = (Math.sin(i / 30d + wave1PhaseShift) * 3) + 4;
			if (direction > .5) {
				height = -height;
			}
			height2 = height + (Math.sin(i / 20d + wave2PhaseShift) * 1);

			if (i == 0) {
				offset = (int) height2;
			}
			height2 -= offset;
			if (height2 > 0) {
				for (int j = 0; j < height2 || j < 1; j++) {
					if (height2 - j > 1) {
						world[j + startHeight][i] = BlockEnum.DIRT;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;
					}
				}
			} else {
				for (int j = 0; j > height2 || j > -1; j--) {
					if (j - height2 > 1) {
						world[j + startHeight][i] = null;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;

					}
				}
			}
		}
		for (; i < NPC.BATTHEW.getColInBiome() + NPC.BATTHEW.getBlocksWide(); i++) {
			if (height2 > 0) {
				for (int j = 0; j < height2 || j < 1; j++) {
					if (height2 - j > 1) {
						world[j + startHeight][i] = BlockEnum.DIRT;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;
					}
				}
			} else {
				for (int j = 0; j > height2 || j > -1; j--) {
					if (j - height2 > 1) {
						world[j + startHeight][i] = null;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;

					}
				}
			}
		}
		for (; i < width; i++) {
			height = (Math.sin(i / 30d + wave1PhaseShift) * 3) + 4;
			if (direction > .5) {
				height = -height;
			}
			height2 = height + (Math.sin(i / 20d + wave2PhaseShift) * 1);

			if (i == 0) {
				offset = (int) height2;
			}
			height2 -= offset;
			if (height2 > 0) {
				for (int j = 0; j < height2 || j < 1; j++) {
					if (height2 - j > 1) {
						world[j + startHeight][i] = BlockEnum.DIRT;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;
					}
				}
			} else {
				for (int j = 0; j > height2 || j > -1; j--) {
					if (j - height2 > 1) {
						world[j + startHeight][i] = null;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;

					}
				}
			}
		}
		//----------------------------------
		
		/*for (int i = 0; i < width; i++) {
			double height = (Math.sin(i / 30d + wave1PhaseShift) * 3) + 4;
			if (direction > .5) {
				height = -height;
			}
			double height2 = height + (Math.sin(i / 20d + wave2PhaseShift) * 1);

			if (i == 0) {
				offset = (int) height2;
			}
			height2 -= offset;
			if (height2 > 0) {
				for (int j = 0; j < height2 || j < 1; j++) {
					if (height2 - j > 1) {
						world[j + startHeight][i] = BlockEnum.DIRT;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;
					}
				}
			} else {
				for (int j = 0; j > height2 || j > -1; j--) {
					if (j - height2 > 1) {
						world[j + startHeight][i] = null;
					} else {
						world[j + startHeight][i] = BlockEnum.GRASS;

					}
				}
			}
		}*/
		return world;
	}

	private static BlockEnum[][] genOcean(int width, int startHeight, int endHeight) {
		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < startHeight; j++) {
				world[j][i] = BlockEnum.SAND;
			}
		}
		for (int i = 25; i < width; i++) {
			double height = -Math.pow((Math.sin((float) (i - 25) * Math.PI / (float) 200)), .65) * (startHeight - 11);
			for (int j = -1; j > height; j--) {
				world[j + startHeight][i] = BlockEnum.WATER;
			}
		}
		if (Math.pow((Math.sin((float) (100) * Math.PI / (float) 200)), .65) * (startHeight - 11) < 8) {
			regenRequired = true;
		}
		return world;
	}

	private static BlockEnum[][] genTown(int width, int startHeight, int endHeight) {
		BlockEnum[][] world = new BlockEnum[WORLDHEIGHT][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < startHeight; j++) {
				world[j][i] = BlockEnum.DIRT;
			}
			world[startHeight - 1][i] = BlockEnum.GRASS;
		}
		return world;
	}

	public static BlockEnum[][] scatterOnSurface(BlockEnum block, BlockEnum[][] world, int num, int minDist) {
		ArrayList<Integer> generatedPositions = new ArrayList<Integer>();
		for (int i = 0; i < num; i++) {
			int pos = (int) (Math.random() * BIOMEWIDTH);
			while (generatedPositions.contains(pos)) {
				pos = (int) (Math.random() * BIOMEWIDTH);
			}
			generatedPositions.add(pos);
			world[Game.getSurfaceRows(world)[pos] + 1][pos] = block;
			//System.out.println(i);
		}
		return world;
	}

	private static ArrayList<Integer> tree(BlockEnum[][] world, int num) {

		ArrayList<Integer> generatedPositions = new ArrayList<Integer>();
		for (int i = 0; i < num; i++) {
			int pos = (int) (Math.random() * BIOMEWIDTH);
			while (generatedPositions.contains(pos) || generatedPositions.contains(pos - 2)
					|| generatedPositions.contains(pos - 1) || generatedPositions.contains(pos + 2)
					|| generatedPositions.contains(pos + 1)) {
				pos = (int) (Math.random() * BIOMEWIDTH);
			}
			generatedPositions.add(pos);
			//System.out.println(i);
		}
		return generatedPositions;

	}

	public static BlockEnum[][] genWorld() {
		regenRequired = false;
		BlockEnum[][] world;
		BlockEnum[][] snow;
		BlockEnum[][] desert;
		BlockEnum[][] grass;
		BlockEnum[][] rainforest;
		BlockEnum[][] ocean;
		BlockEnum[][] town;
		trees = new ArrayList<Integer>();

		world = new BlockEnum[WORLDHEIGHT][BIOMEWIDTH * 6];

		biomeOrder = new ArrayList<Integer>();
		biomeOrder.add(0);
		biomeOrder.add(1);
		biomeOrder.add(2);
		biomeOrder.add(3);
		biomeOrder.add(4);
		Collections.shuffle(biomeOrder);

		biomeOrder.add(3, 5);
		int startHeight = 30;

		for (int i = 0; i < biomeOrder.size(); i++) {
			BlockEnum[][] activeBiome = null;
			if (biomeOrder.get(i) == 0) {
				snow = genSnow(BIOMEWIDTH, startHeight, 50);
				activeBiome = snow;
			} else if (biomeOrder.get(i) == 1) {
				desert = genDesert(BIOMEWIDTH, startHeight, 50);

				activeBiome = desert;
			} else if (biomeOrder.get(i) == 2) {

				grass = genGrass(BIOMEWIDTH, startHeight, 50);
				ArrayList<Integer> grassTrees = tree(grass, 15);
				for (int index = 0; index < grassTrees.size(); index++) {
					trees.add(grassTrees.get(index) + i * BIOMEWIDTH);
				}
				activeBiome = grass;
			}

			else if (biomeOrder.get(i) == 3) {
				rainforest = genRainforest(BIOMEWIDTH, startHeight, 50);
				ArrayList<Integer> rainForestTrees = tree(rainforest, 40);
				for (int index = 0; index < rainForestTrees.size(); index++) {
					trees.add(rainForestTrees.get(index) + i * BIOMEWIDTH);
				}
				activeBiome = rainforest;
			} else if (biomeOrder.get(i) == 4) {
				ocean = genOcean(BIOMEWIDTH, startHeight, 50);
				activeBiome = ocean;
			} else if (biomeOrder.get(i) == 5) {
				town = genTown(BIOMEWIDTH, startHeight, 50);
				activeBiome = town;
			}
			int col = 0;
			while (world[0][col] != null) {
				col++;
			}
			for (int j = 0; j < activeBiome.length; j++) {
				for (int k = 0; k < activeBiome[0].length; k++) {
					world[j][col + k] = activeBiome[j][k];
				}
			}
			startHeight = 0;
			while (world[startHeight][col + activeBiome[0].length - 1] != null) {
				startHeight++;
			}
			startHeight--;
		}
		// print(world);
		if (!regenRequired) {
			return world;
		} else {
			return genWorld();
		}
	}

	public static BlockEnum[][] flipWorld(BlockEnum[][] world) {
		BlockEnum[][] newWorld = new BlockEnum[world.length][world[0].length];
		for (int r = 0; r < newWorld.length; r++) {
			for (int c = 0; c < newWorld[0].length; c++) {
				newWorld[r][c] = world[world.length - 1 - r][c];
			}
		}
		return newWorld;
	}
	
	public static void print(BlockEnum[][] world) {
		for (int i = world.length - 1; i >= 0; i--) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j] != null) {
					System.out.print(world[i][j]);
				} else {
					System.out.print("N");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}