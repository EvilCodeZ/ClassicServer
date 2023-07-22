package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet0Identification implements IPacket {

    private int protocolVersion;
    private String username;
    private String verificationKey;
    private int permissionLevel;

    public Packet0Identification() {}

    public Packet0Identification(int protocolVersion, String username, String verificationKey, int permissionLevel) {
        this.protocolVersion = protocolVersion;
        this.username = username;
        this.verificationKey = verificationKey;
        this.permissionLevel = permissionLevel;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(protocolVersion);
        out.writeString(username);
        out.writeString(verificationKey);
        out.writeByte(permissionLevel);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        protocolVersion = in.readByte();
        username = in.readString();
        verificationKey = in.readString();
        permissionLevel = in.readByte();
    }

    @Override
    public int getSize() {
        return 1 + 64 + 64 + 1;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
