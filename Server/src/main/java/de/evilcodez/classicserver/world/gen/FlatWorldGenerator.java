package de.evilcodez.classicserver.world.gen;

import de.evilcodez.classicserver.world.Level;
import de.evilcodez.classicserver.world.block.Blocks;

public class FlatWorldGenerator implements IWorldGenerator {

    @Override
    public void generate(Level level) {
        for(int y = 0; y <= (int) level.getGroundLevel(); y++) {
            for(int x = 0; x < level.sizeX; x++) {
                for(int z = 0; z < level.sizeZ; z++) {
                    if(y == 0) {
                        level.setBlock(x, y, z, Blocks.BEDROCK.getId());
                        continue;
                    }else if(y == (int) level.getGroundLevel()) {
                        level.setBlock(x, y, z, Blocks.GRASS.getId());
                        continue;
                    }else if(y >= (int) level.getGroundLevel() - 4) {
                        level.setBlock(x, y, z, Blocks.DIRT.getId());
                        continue;
                    }
                    level.setBlock(x, y, z, Blocks.STONE.getId());
                }
            }
        }
    }

    @Override
    public void populate(Level level) {

    }
}
