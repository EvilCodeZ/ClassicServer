package de.evilcodez.classicserver.world.block;

import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;

public abstract class FluidBlock extends Block {

    public FluidBlock(String name, int id) {
        super(name, id, false, 1.0F);
        this.setTickable(true);
        this.setMaterial(BlockMaterial.FLUID);
    }

    @Override
    public void tick(World world, BlockPos pos) {
        if(!world.<Boolean>getGameRule(GameRule.WATER_PHYSICS) && !world.<Boolean>getGameRule(GameRule.LAVA_PHYSICS)) return;

        if(MinecraftServer.getInstance().getRunningTicks() % this.getDelayTicks() != 0) {
            return;
        }

        final Block mx = Blocks.blocks[world.getLevel().getBlock(pos.getX() - 1, pos.getY(), pos.getZ())];
        final Block px = Blocks.blocks[world.getLevel().getBlock(pos.getX() + 1, pos.getY(), pos.getZ())];
        final Block mz = Blocks.blocks[world.getLevel().getBlock(pos.getX(), pos.getY(), pos.getZ() - 1)];
        final Block pz = Blocks.blocks[world.getLevel().getBlock(pos.getX(), pos.getY(), pos.getZ() + 1)];
        final Block my = Blocks.blocks[world.getLevel().getBlock(pos.getX(), pos.getY() - 1, pos.getZ())];

        if(!mx.isSolid() && mx != this) onNeighborBlock(world, pos, pos.subtract(1, 0, 0));
        if(!px.isSolid() && px != this) onNeighborBlock(world, pos, pos.add(1, 0, 0));
        if(!mz.isSolid() && mz != this) onNeighborBlock(world, pos, pos.subtract(0, 0, 1));
        if(!pz.isSolid() && pz != this) onNeighborBlock(world, pos, pos.add(0, 0, 1));
        if(!my.isSolid() && my != this) onNeighborBlock(world, pos, pos.subtract(0, 1, 0));

        super.tick(world, pos);
    }

    public abstract void onNeighborBlock(World world, BlockPos sourcePos, BlockPos neighborPos);

    public abstract int getDelayTicks();
}
