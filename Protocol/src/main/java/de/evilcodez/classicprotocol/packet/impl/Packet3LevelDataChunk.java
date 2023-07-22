package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet3LevelDataChunk implements IPacket {

    private int length;
    private byte[] data;
    private byte progress;

    public Packet3LevelDataChunk() {}

    public Packet3LevelDataChunk(int length, byte[] data, int progress) {
        this.length = length;
        this.data = data;
        this.progress = (byte) progress;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeShort(length);
        out.writeByteArray(data);
        out.writeByte(progress);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        length = in.readShort();
        data = in.readByteArray();
        progress = in.readByte();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 2 + 1024 + 1;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getProgress() {
        return progress;
    }

    public void setProgress(byte progress) {
        this.progress = progress;
    }
}
