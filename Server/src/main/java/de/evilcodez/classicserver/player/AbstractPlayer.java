package de.evilcodez.classicserver.player;

import de.evilcodez.classicprotocol.packet.impl.Packet8PositionRotation;
import de.evilcodez.classicserver.world.World;

public abstract class AbstractPlayer {

    protected final int entityId;
    protected final String name;
    protected final World world;

    public double x, y, z;
    public double prevX, prevY, prevZ;
    public float yaw, pitch;
    public float prevYaw, prevPitch;
    public boolean movementCheck;

    public AbstractPlayer(int entityId, String name, World world) {
        this.entityId = entityId;
        this.name = name;
        this.world = world;
    }

    public int getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public void tick() {
        final boolean moved = x != prevX || y != prevY || z != prevZ;
        final boolean rotated = yaw != prevYaw || pitch != prevPitch;
        if (moved || rotated) {
            final Packet8PositionRotation packet = new Packet8PositionRotation(getEntityId(), x, y, z, yaw, pitch);
            for (AbstractPlayer player : world.getPlayers()) {
                if(player instanceof PlayerEntity && player != this) {
                    ((PlayerEntity) player).getNetworkHandler().sendPacket(packet);
                }
            }
        }
        prevX = x;
        prevY = y;
        prevZ = z;
        prevYaw = yaw;
        prevPitch = pitch;
    }

    public void teleport(double x, double y, double z, float yaw, float pitch) {
        this.updatePosition(x, y, z);
        this.updateRotation(yaw, pitch);
    }

    public void updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.movementCheck = true;
    }

    public void updateRotation(float yaw, float pitch) {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double distanceTo(double x, double y, double z) {
        final double distX = x - this.x;
        final double distY = y - this.y;
        final double distZ = z - this.z;
        return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }
}
