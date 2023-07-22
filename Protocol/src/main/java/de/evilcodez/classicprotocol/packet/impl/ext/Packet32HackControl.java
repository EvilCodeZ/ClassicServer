package de.evilcodez.classicprotocol.packet.impl.ext;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet32HackControl implements IPacket {

    private boolean allowFlying;
    private boolean allowNoClip;
    private boolean speeding;
    private boolean allowRespawn;
    private boolean allowThirdPerson;
    private int jumpHeight;

    public Packet32HackControl() {
    }

    public Packet32HackControl(boolean allowFlying, boolean allowNoClip, boolean speeding, boolean allowRespawn, boolean allowThirdPerson, int jumpHeight) {
        this.allowFlying = allowFlying;
        this.allowNoClip = allowNoClip;
        this.speeding = speeding;
        this.allowRespawn = allowRespawn;
        this.allowThirdPerson = allowThirdPerson;
        this.jumpHeight = jumpHeight;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeBoolean(allowFlying);
        out.writeBoolean(allowNoClip);
        out.writeBoolean(speeding);
        out.writeBoolean(allowRespawn);
        out.writeBoolean(allowThirdPerson);
        out.writeShort(jumpHeight);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        allowFlying = in.readBoolean();
        allowNoClip = in.readBoolean();
        speeding = in.readBoolean();
        allowRespawn = in.readBoolean();
        allowThirdPerson = in.readBoolean();
        jumpHeight = in.readShort();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 7;
    }

    public boolean isAllowFlying() {
        return allowFlying;
    }

    public void setAllowFlying(boolean allowFlying) {
        this.allowFlying = allowFlying;
    }

    public boolean isAllowNoClip() {
        return allowNoClip;
    }

    public void setAllowNoClip(boolean allowNoClip) {
        this.allowNoClip = allowNoClip;
    }

    public boolean isSpeeding() {
        return speeding;
    }

    public void setSpeeding(boolean speeding) {
        this.speeding = speeding;
    }

    public boolean isAllowRespawn() {
        return allowRespawn;
    }

    public void setAllowRespawn(boolean allowRespawn) {
        this.allowRespawn = allowRespawn;
    }

    public boolean isAllowThirdPerson() {
        return allowThirdPerson;
    }

    public void setAllowThirdPerson(boolean allowThirdPerson) {
        this.allowThirdPerson = allowThirdPerson;
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public void setJumpHeight(int jumpHeight) {
        this.jumpHeight = jumpHeight;
    }
}
