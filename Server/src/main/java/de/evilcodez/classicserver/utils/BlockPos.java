package de.evilcodez.classicserver.utils;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class BlockPos {

    @SerializedName("x")
    private final int x;

    @SerializedName("y")
    private final int y;

    @SerializedName("z")
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public BlockPos subtract(BlockPos pos) {
        return new BlockPos(this.x - pos.getX(), this.y - pos.getY(), this.z - pos.getZ());
    }

    public BlockPos subtract(int x, int y, int z) {
        return new BlockPos(this.x - x, this.y - y, this.z - z);
    }

    public BlockPos multiply(int x, int y, int z) {
        return new BlockPos(this.x * x, this.y * y, this.z * z);
    }

    public BlockPos divide(int x, int y, int z) {
        return new BlockPos(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return x == blockPos.x && y == blockPos.y && z == blockPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
