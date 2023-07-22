package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet28SetBlockPermissions implements IPacket {

    private int blockType;
    private boolean allowPlace;
    private boolean allowBreak;

    public Packet28SetBlockPermissions() {
    }

    public Packet28SetBlockPermissions(int blockType, boolean allowPlace, boolean allowBreak) {
        this.blockType = blockType;
        this.allowPlace = allowPlace;
        this.allowBreak = allowBreak;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(blockType);
        out.writeBoolean(allowPlace);
        out.writeBoolean(allowBreak);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        blockType = in.readUnsignedByte();
        allowPlace = in.readBoolean();
        allowBreak = in.readBoolean();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 3;
    }

    public int getBlockType() {
        return blockType;
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
    }

    public boolean isAllowPlace() {
        return allowPlace;
    }

    public void setAllowPlace(boolean allowPlace) {
        this.allowPlace = allowPlace;
    }

    public boolean isAllowBreak() {
        return allowBreak;
    }

    public void setAllowBreak(boolean allowBreak) {
        this.allowBreak = allowBreak;
    }
}
