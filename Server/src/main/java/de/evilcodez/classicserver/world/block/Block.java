package de.evilcodez.classicserver.world.block;

import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.World;

public class Block {

    private final String name;
    private final int id;
    private final double height;
    private final boolean solid;
    private final boolean is15aBlock;
    private BlockMaterial material;
    private boolean isTickable;

    public Block(String name, int id) {
        this(name, id, true, 1.0, false);
    }

    public Block(String name, int id, boolean solid, double height) {
        this(name, id, solid, height, false);
    }

    public Block(String name, int id, boolean is15aBlock) {
        this(name, id, true, 1.0, is15aBlock);
    }

    public Block(String name, int id, boolean solid, double height, boolean is15aBlock) {
        this.name = name;
        this.id = id;
        this.solid = solid;
        this.height = height;
        this.is15aBlock = is15aBlock;
        this.material = BlockMaterial.ROCK;
        Blocks.blocks[id] = this;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isIs15aBlock() {
        return is15aBlock;
    }

    protected void setTickable(boolean tickable) {
        isTickable = tickable;
    }

    public boolean isTickable() {
        return isTickable;
    }

    public BlockMaterial getMaterial() {
        return material;
    }

    protected Block setMaterial(BlockMaterial material) {
        this.material = material;
        return this;
    }

    public void tick(World world, BlockPos pos) {}
}
