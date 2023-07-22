package de.evilcodez.classicserver.world;

import de.evilcodez.classicserver.utils.AxisAlignedBB;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.block.Block;
import de.evilcodez.classicserver.world.block.Blocks;
import de.evilcodez.classicserver.world.gen.NormalWorldGenerator;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Level implements Serializable {

    private final Set<BlockPos> tickList;
    public int sizeX;
    public int sizeZ;
    public int sizeY;
    public byte[] blocks;
    public int waterLevel;

    public Level() {
        this.tickList = Collections.synchronizedSet(new HashSet<>());
    }

    public void setData(int var1, int var2, int var3, byte[] blockData) {
        this.tickList.clear();
        this.sizeX = var1;
        this.sizeZ = var3;
        this.sizeY = var2;
        this.blocks = blockData;
        waterLevel = (int) (NormalWorldGenerator.WATER_VAL * (float) sizeY / 1.5F);

        for(int y = 0; y < sizeY; ++y) {
            for(int x = 0; x < sizeX; ++x) {
                for(int z = 0; z < sizeZ; ++z) {
                    if(Blocks.blocks[this.getBlock(x, y, z)].isTickable()) {
                        this.addToTickList(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }

    public boolean setBlock(int x, int y, int z, int block) {
        if (x >= 0 && y >= 0 && z >= 0 && x < this.sizeX && y < this.sizeY && z < this.sizeZ) {
            if (block == this.blocks[(y * this.sizeZ + z) * this.sizeX + x]) {
                return false;
            } else {
                final BlockPos pos = new BlockPos(x, y, z);
                if(!Blocks.blocks[this.getBlock(x, y, z)].isTickable()) {
                    this.removeFromTickList(pos);
                }
                this.blocks[(y * this.sizeZ + z) * this.sizeX + x] = (byte) block;
                if(Blocks.blocks[block].isTickable()) {
                    this.addToTickList(pos);
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public Block getBlock(BlockPos pos) {
        return Blocks.blocks[this.getBlock(pos.getX(), pos.getY(), pos.getZ())];
    }

    public int getBlock(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < this.sizeX && y < this.sizeY && z < this.sizeZ
                ? this.blocks[(y * this.sizeZ + z) * this.sizeX + x] & 255
                : 0;
    }

    public float getGroundLevel() {
        return this.getWaterLevel() - 2.0F;
    }

    public float getWaterLevel() {
        return (float) this.waterLevel;
    }

    public static byte[] decompress(InputStream var0) {
        try {
            DataInputStream var3;
            byte[] var1 = new byte[(var3 = new DataInputStream(new GZIPInputStream(var0))).readInt()];
            var3.readFully(var1);
            var3.close();
            return var1;
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public static byte[] compress(Level level) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(baos));
            out.writeInt(level.blocks.length);
            out.write(level.blocks);
            out.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setSize(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks = new byte[sizeX * sizeY * sizeZ];
        this.tickList.clear();
        waterLevel = (int) (NormalWorldGenerator.WATER_VAL * (float) sizeY / 1.5F);
    }

    public List<AxisAlignedBB> getBlockCollisionBoxes(AxisAlignedBB boundingBox) {
        final List<AxisAlignedBB> list = new ArrayList<>();
        int startX = (int) (Math.floor(boundingBox.minX) - 1);
        int endX = (int) (Math.ceil(boundingBox.maxX) + 1);
        int startY = (int) (Math.floor(boundingBox.minY) - 1);
        int endY = (int) (Math.ceil(boundingBox.maxY) + 1);
        int startZ = (int) (Math.floor(boundingBox.minZ) - 1);
        int endZ = (int) (Math.ceil(boundingBox.maxZ) + 1);

        for (int xx = startX; xx < endX; ++xx) {
            for (int zz = startZ; zz < endZ; ++zz) {
                boolean isStartOrEndX = xx == startX || xx == endX - 1;
                boolean isStartOrEndZ = zz == startZ || zz == endZ - 1;

                if (!isStartOrEndX || !isStartOrEndZ) {
                    for (int yy = startY; yy < endY; ++yy) {
                        if (!isStartOrEndX && !isStartOrEndZ || yy != endY - 1) {

                            Block state;

                            // If coordinate isn't in world border
                            if(xx < 0 || zz < 0 || xx >= this.sizeX || zz >= this.sizeZ) {
                                state = Blocks.STONE;
                            }else {
                                state = Blocks.blocks[this.getBlock(xx, yy, zz)];
                            }

                            if(state.getHeight() > 0 && state.isSolid()) {
                                list.add(new AxisAlignedBB(
                                        0, 0, 0,
                                        1, state.getHeight(), 1
                                ).offset(xx, yy, zz));
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    public Map<BlockPos, Block> getBlocksInBB(AxisAlignedBB boundingBox) {
        final Map<BlockPos, Block> list = new HashMap<>();
        int startX = (int) (Math.floor(boundingBox.minX) - 1);
        int endX = (int) (Math.ceil(boundingBox.maxX) + 1);
        int startY = (int) (Math.floor(boundingBox.minY) - 1);
        int endY = (int) (Math.ceil(boundingBox.maxY) + 1);
        int startZ = (int) (Math.floor(boundingBox.minZ) - 1);
        int endZ = (int) (Math.ceil(boundingBox.maxZ) + 1);

        for (int xx = startX; xx < endX; ++xx) {
            for (int zz = startZ; zz < endZ; ++zz) {
                boolean isStartOrEndX = xx == startX || xx == endX - 1;
                boolean isStartOrEndZ = zz == startZ || zz == endZ - 1;

                if (!isStartOrEndX || !isStartOrEndZ) {
                    for (int yy = startY; yy < endY; ++yy) {
                        if (!isStartOrEndX && !isStartOrEndZ || yy != endY - 1) {

                            Block state;

                            // If coordinate isn't in world border
                            if(xx < 0 || zz < 0 || xx >= this.sizeX || zz >= this.sizeZ) {
                                state = Blocks.STONE;
                            }else {
                                state = Blocks.blocks[this.getBlock(xx, yy, zz)];
                            }

                            if(state.getHeight() > 0 && state.isSolid()) {
                                list.put(new BlockPos(xx, yy, zz), state);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    public void addToTickList(BlockPos pos) {
        this.tickList.add(pos);
    }

    public void removeFromTickList(BlockPos pos) {
        this.tickList.remove(pos);
    }

    public Set<BlockPos> getTickList() {
        return tickList;
    }

    public boolean isPositionInLevel(BlockPos pos) {
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < this.sizeX && pos.getY() < this.sizeY && pos.getZ() < this.sizeZ;
    }
}
