package de.evilcodez.classicserver.world.gen;

import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.Level;
import de.evilcodez.classicserver.world.block.Blocks;
import me.dags.noise.Noise;
import me.dags.noise.Source;
import me.dags.noise.source.FastSimplex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalWorldGenerator implements IWorldGenerator {
    
    public static float WATER_VAL = 0.715F;
    
    @Override
    public void generate(Level level) {
        final int maxProgress = level.sizeX;
        int progress = 0;
        int highestY = 0;

        final Noise perlin = Source.perlin(150, 3 + (int) (Math.random() * 5.0));
        for (int x = 0; x < level.sizeX; x++) {
            for (int z = 0; z < level.sizeZ; z++) {
                level.setBlock(x, 0, z, Blocks.BEDROCK.getId());
                float val = perlin.getValue(x, z);
                val = (float) Math.pow(val, 0.31F);

                int yStart = (int) (val * level.sizeY / 1.5F);
                if(yStart > highestY) {
                    highestY = yStart;
                }

                if(val < (WATER_VAL+ 0.03F) && val >= WATER_VAL) {
                    level.setBlock(x, yStart, z, Blocks.SAND.getId());
                }else if(val >= (WATER_VAL+ 0.03F)) {
                    level.setBlock(x, yStart, z, Blocks.GRASS.getId());
                }
                final float waterY = WATER_VAL * level.sizeY / 1.5F;
                for (int yy = yStart - 1; yy > yStart - 3 && yy > 0; yy--) {
                    if(val < WATER_VAL) {
                        for(int yy2 = yy; yy2 <= waterY; ++yy2) {
                            level.setBlock(x, yy2, z, Blocks.WATER.getId());
                        }
                        break;
                    }else if(val < (WATER_VAL + 0.03F)) {
                        level.setBlock(x, yy, z, Blocks.SAND.getId());
                    }else {
                        level.setBlock(x, yy, z, Blocks.DIRT.getId());
                    }
                }
                boolean did = false;
                for (int yy = yStart - 3; yy > 0; yy--) {
                    if(val < WATER_VAL && !did) {
                        yy += 2;
                        did = true;
                        level.setBlock(x, yy, z, Blocks.GRAVEL.getId());
                        continue;
                    }
                    level.setBlock(x, yy, z, Blocks.STONE.getId());
                }
            }
            ++progress;
            if(progress % 10 == 0) {
                System.out.println("Generating world " + String.format("%.1f", (float) progress / (float) maxProgress * 100.0F) + "%...");
            }
        }

        final FastSimplex cave = (FastSimplex) Source.simplex(100, 4 + (int) (Math.random() * 5.0));
        for(int z = 0; z < level.sizeZ; ++z) {
            for (int y = 0; y < highestY; ++y) {
                for (int x = 0; x < level.sizeX; ++x) {
                    final float val = cave.getValue(x, y) * cave.getValue(z, y);
                    if (val > 0.54) {
                        level.setBlock(x, y, z, 0);
                    }
                }
            }
        }
    }

    @Override
    public void populate(Level level) {
        final List<BlockPos> trees = new ArrayList<>();
        final Random rnd = new Random();
        for(int x = 0; x < level.sizeX; ++x) {
            for(int z = 0; z < level.sizeZ; ++z) {
                int highestY = level.sizeY - 1;
                while(highestY > 0) {
                    if(level.getBlock(x, highestY, z) != 0) {
                        break;
                    }
                    --highestY;
                }
                if(level.getBlock(x, highestY, z) == Blocks.GRASS.getId()) {
                    if(rnd.nextInt(24) == 11) {
                        int blockId;
                        switch (rnd.nextInt(10)) {
                            case 0:
                            case 3:
                            case 6:
                            case 9:
                            default:
                                blockId = Blocks.DANDELION.getId();
                                break;
                            case 1:
                            case 4:
                            case 8:
                                blockId = Blocks.ROSE.getId();
                                break;
                            case 2:
                                blockId = Blocks.BROWN_MUSHROOM.getId();
                                break;
                            case 5:
                                blockId = Blocks.RED_MUSHROOM.getId();
                                break;
                            case 7:
                                blockId = Blocks.SAPLING.getId();
                                break;
                        }
                        level.setBlock(x, highestY + 1, z, blockId);
                    }
                    if(rnd.nextInt(40) == 34) {
                        double lowestDistance = Double.MAX_VALUE;
                        for (final BlockPos tree : trees) {
                            final double dx = tree.getX() - x;
                            final double dz = tree.getZ() - z;
                            final double dist = Math.sqrt(dx * dx + dz * dz);
                            if(dist < lowestDistance) {
                                lowestDistance = dist;
                            }
                        }
                        if(lowestDistance < 6.0) {
                            continue;
                        }
                        generateTree(rnd, level, x, highestY + 1, z);
                        trees.add(new BlockPos(x, highestY + 1, z));
                    }
                }
            }
        }
    }

    public static void generateTree(Random rnd, Level level, int x, int y, int z) {
        final int treeHeight = 3 + rnd.nextInt(4);

        for(int yy = y; yy <= y + treeHeight; ++yy) {
            level.setBlock(x, yy, z, Blocks.LOG.getId());
        }

        final int sub = treeHeight > 5 ? 2 + rnd.nextInt(2) : 2;
        for(int yy = y + treeHeight - sub; yy <= y + treeHeight + 1; ++yy) {
            int rad = yy == y + treeHeight + 1 ? 1 : 2;
            for(int xx = x - rad; xx <= x + rad; ++xx) {
                for(int zz = z - rad; zz <= z + rad; ++zz) {
                    if(level.getBlock(xx, yy, zz) == 0) {

                        boolean isOnEdge = (yy == y + treeHeight || yy == y + treeHeight - sub)
                                && ((xx == x - rad || xx == x + rad) && (zz == z - rad || zz == z + rad));
                        if(isOnEdge && rnd.nextBoolean()) {
                            continue;
                        }

                        level.setBlock(xx, yy, zz, Blocks.LEAVES.getId());
                    }
                }
            }
        }
        level.setBlock(x + 1, y + treeHeight + 2, z, Blocks.LEAVES.getId());
        level.setBlock(x, y + treeHeight + 2, z - 1, Blocks.LEAVES.getId());
        level.setBlock(x, y + treeHeight + 2, z + 1, Blocks.LEAVES.getId());
        level.setBlock(x - 1, y + treeHeight + 2, z, Blocks.LEAVES.getId());
        level.setBlock(x, y + treeHeight + 2, z, Blocks.LEAVES.getId());
        if(sub != 2) {
            level.setBlock(x + 1, y + treeHeight - sub - 1, z, Blocks.LEAVES.getId());
            level.setBlock(x, y + treeHeight - sub - 1, z - 1, Blocks.LEAVES.getId());
            level.setBlock(x, y + treeHeight - sub - 1, z + 1, Blocks.LEAVES.getId());
            level.setBlock(x - 1, y + treeHeight - sub - 1, z, Blocks.LEAVES.getId());
        }
    }
}
