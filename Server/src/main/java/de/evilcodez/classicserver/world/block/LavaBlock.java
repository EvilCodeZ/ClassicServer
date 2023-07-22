package de.evilcodez.classicserver.world.block;

import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;

public class LavaBlock extends FluidBlock {

    public LavaBlock(String name, int id) {
        super(name, id);
    }

    @Override
    public void onNeighborBlock(World world, BlockPos sourcePos, BlockPos neighborPos) {
        if(!world.<Boolean>getGameRule(GameRule.LAVA_PHYSICS)) return;
        final Block block = world.getLevel().getBlock(neighborPos);
        if(block instanceof LavaBlock) return;
        if(block == Blocks.WATER) {
            if(sourcePos.subtract(neighborPos).getY() == 1) {
                world.updateBlock(sourcePos, Blocks.STONE.getId());
            }
            return;
        }
        world.updateBlock(neighborPos.getX(), neighborPos.getY(), neighborPos.getZ(), Blocks.LAVA.getId());
    }

    @Override
    public int getDelayTicks() {
        return 4;
    }
}
