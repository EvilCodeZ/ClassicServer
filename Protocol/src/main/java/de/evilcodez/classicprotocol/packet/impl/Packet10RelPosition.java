package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet10RelPosition implements IPacket {

    private int entityId;
    private double x;
    private double y;
    private double z;

    public Packet10RelPosition() {}

    public Packet10RelPosition(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
        out.writeByte((int) (x * 32.0D));
        out.writeByte((int) (y * 32.0D));
        out.writeByte((int) (z * 32.0D));
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
        x = in.readByte() / 32.0D;
        y = in.readByte() / 32.0D;
        z = in.readByte() / 32.0D;
    }

    @Override
    public int getSize() {
        return 1 + 1 + 1 + 1;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
