package de.evilcodez.classicserver.network;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class FlushControlPacket implements IPacket {

    private FlushMode mode;

    public FlushControlPacket(FlushMode mode) {
        this.mode = mode;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(IPacketHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException();
    }

    public FlushMode getMode() {
        return mode;
    }

    public void setMode(FlushMode mode) {
        this.mode = mode;
    }

    public enum FlushMode {
        ENABLE_FLUSH,
        DISABLE_FLUSH,
        FLUSH,
        ONLY_FLUSH;
    }
}
