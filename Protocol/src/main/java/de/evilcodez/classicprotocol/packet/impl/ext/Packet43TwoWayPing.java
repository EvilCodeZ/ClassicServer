package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet43TwoWayPing implements IPacket {

    private int direction;
    private int data;

    public Packet43TwoWayPing() {
    }

    public Packet43TwoWayPing(int direction, int data) {
        this.direction = direction;
        this.data = data;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(direction);
        out.writeShort(data);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        direction = in.readUnsignedByte();
        data = in.readUnsignedShort();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 3;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
