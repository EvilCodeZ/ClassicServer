package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet16ExtInfo implements IPacket {

    private String appName;
    private int extensionCount;

    public Packet16ExtInfo() {}

    public Packet16ExtInfo(String appName, int extensionCount) {
        this.appName = appName;
        this.extensionCount = extensionCount;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeString(appName);
        out.writeShort(extensionCount);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        appName = in.readString();
        extensionCount = in.readUnsignedShort();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 66;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getExtensionCount() {
        return extensionCount;
    }

    public void setExtensionCount(int extensionCount) {
        this.extensionCount = extensionCount;
    }
}
