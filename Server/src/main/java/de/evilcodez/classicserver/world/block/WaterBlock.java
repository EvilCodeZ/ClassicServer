package de.evilcodez.classicserver.world.block;

import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;

public class WaterBlock extends FluidBlock {

    public WaterBlock(String name, int id) {
        super(name, id);
    }

    @Override
    public void onNeighborBlock(World world, BlockPos sourcePos, BlockPos neighborPos) {
        if(!world.<Boolean>getGameRule(GameRule.WATER_PHYSICS)) return;
        final Block block = world.getLevel().getBlock(neighborPos);
        if(block instanceof WaterBlock) return;
        if(block == Blocks.LAVA) {
            world.updateBlock(neighborPos, Blocks.COBBLESTONE.getId());
            return;
        }
        world.updateBlock(neighborPos.getX(), neighborPos.getY(), neighborPos.getZ(), Blocks.WATER.getId());
    }

    @Override
    public int getDelayTicks() {
        return 2;
    }
}
