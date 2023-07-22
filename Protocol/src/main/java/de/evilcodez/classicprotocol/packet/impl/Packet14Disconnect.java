package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet14Disconnect implements IPacket {

    private String reason;

    public Packet14Disconnect() {}

    public Packet14Disconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeString(reason);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        reason = in.readString();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 64;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
