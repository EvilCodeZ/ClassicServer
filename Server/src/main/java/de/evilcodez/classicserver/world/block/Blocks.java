package de.evilcodez.classicserver.world.block;

public class Blocks {
	
	public static final Block[] blocks = new Block[50];
	
	// Blocks
	public static final Block AIR = new Block("air", 0, false, 0.0, true);
	public static final Block STONE = new Block("stone", 1, true);
	public static final Block GRASS = new Block("grass", 2, true);
	public static final Block DIRT = new Block("dirt", 3, true);
	public static final Block COBBLESTONE = new Block("cobblestone", 4, true);
	public static final Block WOOD = new Block("wood", 5, true);
	public static final Block SAPLING = new Block("sapling", 6, false, 0.0, true);
	public static final Block BEDROCK = new Block("bedrock", 7, true);
	public static final WaterBlock WATER = new WaterBlock("water", 8);
	public static final Block STATIONARY_WATER = new Block("stationary_water", 9, false, 0.0, true).setMaterial(BlockMaterial.FLUID);
	public static final Block LAVA = new LavaBlock("lava", 10);
	public static final Block STATIONARY_LAVA = new Block("stationary_lava", 11, false, 0.0, true).setMaterial(BlockMaterial.FLUID);
	public static final Block SAND = new Block("sand", 12, true);
	public static final Block GRAVEL = new Block("gravel", 13, true);
	public static final Block GOLD_ORE = new Block("gold_ore", 14, true);
	public static final Block IRON_ORE = new Block("iron_ore", 15, true);
	public static final Block COAL_ORE = new Block("coal_ore", 16, true);
	public static final Block LOG = new Block("log", 17, true);
	public static final Block LEAVES = new Block("leaves", 18, true);
	public static final Block SPONGE = new Block("sponge", 19, true);
	public static final Block GLASS = new Block("glass", 20, true);
	
	public static final Block RED_WOOL = new Block("red_wool", 21);
	public static final Block ORANGE_WOOL = new Block("orange_wool", 22);
	public static final Block YELLOW_WOOL = new Block("yellow_wool", 23);
	public static final Block LIME_WOOL = new Block("lime_wool", 24);
	public static final Block GREEN_WOOL = new Block("green_wool", 25);
	public static final Block AQUA_GREEN_WOOL = new Block("aqua_green_wool", 26);
	public static final Block CYAN_WOOL = new Block("cyan_wool", 27);
	public static final Block BLUE_WOOL = new Block("blue_wool", 28);
	public static final Block PURPLE_WOOL = new Block("purple_wool", 29);
	public static final Block INDIGO_WOOL = new Block("indigo_wool", 30);
	public static final Block VIOLET_WOOL = new Block("violet_wool", 31);
	public static final Block MAGENTA_WOOL = new Block("magenta_wool", 32);
	public static final Block PINK_WOOL = new Block("pink_wool", 33);
	public static final Block BLACK_WOOL = new Block("black_wool", 34);
	public static final Block GRAY_WOOL = new Block("gray_wool", 35);
	public static final Block WHITE_WOOL = new Block("white_wool", 36);
	
	public static final Block DANDELION = new Block("dandelion", 37, false, 0.0);
	public static final Block ROSE = new Block("rose", 38, false, 0.0);
	public static final Block BROWN_MUSHROOM = new Block("brown_mushroom", 39, false, 0.0);
	public static final Block RED_MUSHROOM = new Block("red_mushroom", 40, false, 0.0);
	public static final Block GOLD_BLOCK = new Block("gold_ore", 41);
	public static final Block IRON_BLOCK = new Block("iron_ore", 42);
	public static final Block DOUBLE_SLAB = new Block("double_slab", 43);
	public static final Block SLAB = new Block("slab", 44, true, 0.5);
	public static final Block BRICK = new Block("brick", 45);
	public static final TntBlock TNT = new TntBlock("tnt", 46);
	public static final Block BOOKSHELF = new Block("bookshelf", 47);
	public static final Block MOSSY_COBBLESTONE = new Block("mossy_cobblestone", 48);
	public static final Block OBSIDIAN = new Block("obsidian", 49);

	public static boolean isPlaceable(Block block) {
		return !(block == Blocks.BEDROCK
				|| block == Blocks.WATER
				|| block == Blocks.STATIONARY_WATER
				|| block == Blocks.LAVA
				|| block == Blocks.STATIONARY_LAVA
				|| block == Blocks.GRASS
				|| block == Blocks.DOUBLE_SLAB
				|| block == Blocks.AIR);
	}

	public static boolean isBreakable(Block block) {
		return !block.equals(BEDROCK) && block.getMaterial() != BlockMaterial.FLUID;
	}
}
