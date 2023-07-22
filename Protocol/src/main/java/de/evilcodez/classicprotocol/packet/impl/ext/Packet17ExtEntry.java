package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet17ExtEntry implements IPacket {

    private String name;
    private int version;

    public Packet17ExtEntry() {
    }

    public Packet17ExtEntry(String name, int version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeString(name);
        out.writeInt(version);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        name = in.readString();
        version = in.readInt();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 68;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
