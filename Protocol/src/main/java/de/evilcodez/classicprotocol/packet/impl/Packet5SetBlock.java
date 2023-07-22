package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet5SetBlock implements IPacket {

    private int x;
    private int y;
    private int z;
    private boolean place;
    private int blockId;

    public Packet5SetBlock() {}

    public Packet5SetBlock(int x, int y, int z, boolean place, int blockId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.place = place;
        this.blockId = blockId;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeShort(x);
        out.writeShort(y);
        out.writeShort(z);
        out.writeBoolean(place);
        out.writeByte(blockId);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        x = in.readUnsignedShort();
        y = in.readUnsignedShort();
        z = in.readUnsignedShort();
        place = in.readBoolean();
        blockId = in.readUnsignedByte();
    }

    @Override
    public int getSize() {
        return 2 + 2 + 2 + 1 + 1;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isPlace() {
        return place;
    }

    public void setPlace(boolean place) {
        this.place = place;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}
