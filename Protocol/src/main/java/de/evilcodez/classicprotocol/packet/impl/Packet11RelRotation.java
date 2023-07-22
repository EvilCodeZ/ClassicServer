package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet11RelRotation implements IPacket {

    private int entityId;
    private float yaw;
    private float pitch;

    public Packet11RelRotation() {}

    public Packet11RelRotation(int entityId, float yaw, float pitch) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
        out.writeByte((byte) ((byte) (yaw / 360.0F * 256.0F) + 128));
        out.writeByte((byte) (pitch / 360.0F * 256.0F));
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
        yaw = (in.readByte() - 128) / 256.0F * 360.0F;
        pitch = in.readByte() / 256.0F * 360.0F;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1 + 1 + 1;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
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
