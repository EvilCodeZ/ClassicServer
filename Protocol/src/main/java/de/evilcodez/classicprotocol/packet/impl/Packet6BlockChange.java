package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet6BlockChange implements IPacket {

    private int x;
    private int y;
    private int z;
    private int blockId;

    public Packet6BlockChange() {}

    public Packet6BlockChange(int x, int y, int z, int blockId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeShort(x);
        out.writeShort(y);
        out.writeShort(z);
        out.writeByte(blockId);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        x = in.readUnsignedShort();
        y = in.readUnsignedShort();
        z = in.readUnsignedShort();
        blockId = in.readUnsignedByte();
    }

    @Override
    public int getSize() {
        return 2 + 2 + 2 + 1;
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

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}
