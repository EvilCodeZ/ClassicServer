package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet4LevelFinalize implements IPacket {

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public Packet4LevelFinalize() {}

    public Packet4LevelFinalize(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeShort(sizeX);
        out.writeShort(sizeY);
        out.writeShort(sizeZ);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        sizeX = in.readUnsignedShort();
        sizeY = in.readUnsignedShort();
        sizeZ = in.readUnsignedShort();
    }

    @Override
    public int getSize() {
        return 2 + 2 + 2;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public void setSizeZ(int sizeZ) {
        this.sizeZ = sizeZ;
    }
}
