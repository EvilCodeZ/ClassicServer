package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet7SpawnPlayer implements IPacket {

    private int entityId;
    private String username;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Packet7SpawnPlayer() {}

    public Packet7SpawnPlayer(int entityId, String username, double x, double y, double z, float yaw, float pitch) {
        this.entityId = entityId;
        this.username = username;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
        out.writeString(username);
        if(out.getConnection().getPacketRegistry().isExtensionEnabled(ProtocolExtension.EXT_ENTITY_POSITIONS)) {
            out.writeInt((int) (x * 32.0D));
            out.writeInt((int) (y * 32.0D));
            out.writeInt((int) (z * 32.0D));
        }else {
            out.writeShort((int) (x * 32.0D));
            out.writeShort((int) (y * 32.0D));
            out.writeShort((int) (z * 32.0D));
        }
        out.writeByte((byte) ((byte) (yaw / 360.0F * 256.0F) + 128));
        out.writeByte((byte) (pitch / 360.0F * 256.0F));
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
        username = in.readString();
        if(in.getConnection().getPacketRegistry().isExtensionEnabled(ProtocolExtension.EXT_ENTITY_POSITIONS)) {
            x = in.readInt() / 32.0D;
            y = in.readInt() / 32.0D;
            z = in.readInt() / 32.0D;
        }else {
            x = in.readShort() / 32.0D;
            y = in.readShort() / 32.0D;
            z = in.readShort() / 32.0D;
        }
        yaw = (in.readByte() - 128) / 256.0F * 360.0F;
        pitch = in.readByte() / 256.0F * 360.0F;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1 + 64 + 2 + 2 + 2 + 1 + 1;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
