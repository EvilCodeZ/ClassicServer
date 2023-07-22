package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.impl.Packet6BlockChange;

import java.util.List;

public class Packet38BulkBlockUpdate implements IPacket {

    private int updates;
    private byte[] positions;
    private byte[] blocks;

    public Packet38BulkBlockUpdate() {
    }

    public Packet38BulkBlockUpdate(byte[] positions, byte[] blocks) {
        this.positions = positions;
        this.blocks = blocks;
    }

    public Packet38BulkBlockUpdate(int sizeX, int sizeZ, List<Packet6BlockChange> changes, int offset) {
        final int size = Math.min(256, changes.size() - offset);

        this.updates = size;
        this.positions = new byte[1024];
        this.blocks = new byte[256];

        for (int i = offset; i < offset + size; i++) {
            final Packet6BlockChange update = changes.get(i);
            final int index = (update.getY() * sizeZ + update.getZ()) * sizeX + update.getX();
            final int xPos = index % sizeX;
            final int zPos = (index / sizeX) % sizeZ;
            final int yPos = index / (sizeX * sizeZ);
            final int idx = i - offset;
            this.positions[idx * 4] = (byte) ((index >>> 24) & 0xFF);
            this.positions[idx * 4 + 1] = (byte) ((index >>> 16) & 0xFF);
            this.positions[idx * 4 + 2] = (byte) ((index >>> 8) & 0xFF);
            this.positions[idx * 4 + 3] = (byte) (index & 0xFF);
            this.blocks[idx] = (byte) (update.getBlockId() & 0xFF);
        }
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(updates - 1);
        out.write(positions, 0, 1024);
        out.write(blocks, 0, 256);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        updates = in.readUnsignedByte() + 1;
        positions = new byte[1024];
        in.readFully(positions, 0, 1024);
        blocks = new byte[256];
        in.readFully(blocks, 0, 256);
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1281;
    }

    public int getUpdates() {
        return updates;
    }

    public void setUpdates(int updates) {
        this.updates = updates;
    }

    public byte[] getPositions() {
        return positions;
    }

    public void setPositions(byte[] positions) {
        this.positions = positions;
    }

    public byte[] getBlocks() {
        return blocks;
    }

    public void setBlocks(byte[] blocks) {
        this.blocks = blocks;
    }
}
