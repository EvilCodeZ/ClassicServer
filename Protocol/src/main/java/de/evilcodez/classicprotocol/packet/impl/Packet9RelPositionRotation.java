package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet9RelPositionRotation implements IPacket {

    private int entityId;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Packet9RelPositionRotation() {}

    public Packet9RelPositionRotation(int entityId, double x, double y, double z, float yaw, float pitch) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
        out.writeByte((int) (x * 32.0D));
        out.writeByte((int) (y * 32.0D));
        out.writeByte((int) (z * 32.0D));
        out.writeByte((byte) ((byte) (yaw / 360.0F * 256.0F) + 128));
        out.writeByte((byte) (pitch / 360.0F * 256.0F));
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
        x = in.readByte() / 32.0D;
        y = in.readByte() / 32.0D;
        z = in.readByte() / 32.0D;
        yaw = (in.readByte() - 128) / 256.0F * 360.0F;
        pitch = in.readByte() / 256.0F * 360.0F;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1 + 1 + 1 + 1 + 1 + 1;
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

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
