package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet46SetSpawnPoint implements IPacket {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Packet46SetSpawnPoint() {
    }

    public Packet46SetSpawnPoint(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeShort((int) (x * 32.0));
        out.writeShort((int) (y * 32.0));
        out.writeShort((int) (z * 32.0));
        out.writeByte((byte) ((byte) (yaw / 360.0F * 256.0F) + 128));
        out.writeByte((byte) (pitch / 360.0F * 256.0F));
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        x = in.readShort() / 32.0;
        y = in.readShort() / 32.0;
        z = in.readShort() / 32.0;
        yaw = (in.readByte() - 128) / 256.0F * 360.0F;
        pitch = in.readByte() / 256.0F * 360.0F;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 8;
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
