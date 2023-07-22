package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet15UpdatePlayerType implements IPacket {

    private byte permissionLevel;

    public Packet15UpdatePlayerType() {}

    public Packet15UpdatePlayerType(int permissionLevel) {
        this.permissionLevel = (byte) permissionLevel;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(permissionLevel);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        permissionLevel = in.readByte();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1;
    }

    public byte getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = (byte) permissionLevel;
    }
}
