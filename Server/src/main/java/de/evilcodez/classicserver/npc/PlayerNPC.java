package de.evilcodez.classicserver.npc;

import de.evilcodez.classicprotocol.packet.impl.Packet8PositionRotation;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.utils.AxisAlignedBB;
import de.evilcodez.classicserver.world.World;

import java.util.List;
import java.util.Random;

public class PlayerNPC extends AbstractPlayer {

    public double motionX, motionY, motionZ;
    public boolean onGround;
    public boolean collided, collidedHorizontally, collidedVertically;
    public int age;

    public float forwardSpeed;
    public float strafeSpeed;
    public boolean jumping;

    private int currentTask;
    private int taskDuration;

    public PlayerNPC(String name, World world, int entityId) {
        super(entityId, name, world);
    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch) {
        super.teleport(x, y, z, yaw, pitch);
        this.world.broadcastPacket(new Packet8PositionRotation(entityId, x, y + 1.62, z, yaw, pitch));
    }

    public void tick() {
        ++this.age;

        this.tickTask();

        motionY -= 0.08D;
        final double d = onGround ? 0.2D : 0.98D;
        motionX *= d;
        motionY *= 0.98D;
        motionZ *= d;

        this.handleMovement();

        this.movePlayer(motionX, motionY, motionZ);
        this.y = Math.max(0, y);

        final boolean moved = x != prevX || y != prevY || z != prevZ;
        final boolean rotated = yaw != prevYaw || pitch != prevPitch;
        if(moved || rotated) {
            this.world.broadcastPacket(new Packet8PositionRotation(entityId, x, y + 1.62, z, yaw, pitch));
        }
    }

    private void tickTask() {
        final Random random = new Random();
        if(taskDuration < 1) {
            this.taskDuration = 0;
            this.currentTask = random.nextInt(4);

            switch (currentTask) {
                case 0: // Idle task
                    this.taskDuration = random.nextInt(41); // Max 2 seconds
                    break;
                case 1: // Look at entity task
                    this.taskDuration = 20 + random.nextInt(121); // Max 7 seconds
                    break;
                case 2: // Random look
                    this.yaw += (random.nextFloat() - 0.5f) * 90.0f;
                    this.pitch = 0.0f;
                    return;
                case 3: // Random walk
                    this.taskDuration = 20 + random.nextInt(201); // Max 11 seconds
                    break;
            }
        }

        if(currentTask == 0) {
            // Idle
        }else if(currentTask == 1) {
            // Look at entity
            final AbstractPlayer nearest = this.getNearestPlayer(6.0);
            if(nearest == null) {
                this.taskDuration = 0;
                return;
            }
            double yPos = nearest.y + (nearest instanceof PlayerNPC ? 1.62 : 0.0);
            this.lookAt(nearest.x, yPos, nearest.z);
        }else if(currentTask == 3) {
            // Random walk
            this.forwardSpeed = 1.5f;
            if(this.collidedHorizontally || random.nextInt(60) < 3) this.jumping = true;
            if(random.nextInt(10) < 3) {
                this.yaw += (random.nextFloat() - 0.5f) * 45.0f;
            }
        }

        --this.taskDuration;
    }

    private AbstractPlayer getNearestPlayer(double range) {
        AbstractPlayer nearest = null;
        double smallestRange = Double.MAX_VALUE;
        for (AbstractPlayer player : this.world.getPlayers()) {
            if(player == this) continue;
            final double d = this.distanceTo(player.x, player.y, player.z);
            if(d <= range && d < smallestRange) {
                smallestRange = d;
                nearest = player;
            }
        }
        return nearest;
    }

    public void lookAt(double x, double y, double z) {
        final double diffX = x - this.x;
        final double diffY = y - (this.y + 1.62);
        final double diffZ = z - this.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        yaw -= this.yaw;
        this.yaw += yaw / 15.0f;
        this.pitch = pitch;
    }

    public void move(double x, double z) {
        final double yaw = Math.toRadians(this.yaw);
        final double d = Math.atan2(z - this.z, x - this.x);
        this.forwardSpeed = (float) Math.sin(d + yaw);
        this.strafeSpeed = (float) Math.cos(d - yaw);
    }

    private void handleMovement() {
        final double d0 = Math.PI / 180.0D;
        final double d1 = Math.sin(this.yaw * d0);
        final double d2 = Math.cos(this.yaw * d0);
        this.motionX += this.strafeSpeed * d2 - this.forwardSpeed * d1;
        this.motionZ += this.forwardSpeed * d2 - this.strafeSpeed * d1;
        this.motionX *= 0.16277136D;
        this.motionZ *= 0.16277136D;

        if(this.jumping && this.onGround) this.jump();

        this.jumping = false;
        this.forwardSpeed = 0.0F;
        this.strafeSpeed = 0.0F;
    }

    private void movePlayer(double newX, double newY, double newZ) {
        AxisAlignedBB boundingBox = new AxisAlignedBB(
                this.x - 0.3D, this.y, this.z - 0.3D,
                this.x + 0.3D, this.y + 2.0D, this.z + 0.3D
        );
        final List<AxisAlignedBB> colliding = this.world.getLevel().getBlockCollisionBoxes(boundingBox.addCoord(newX, newY, newZ));

        double oldX = newX;
        double oldY = newY;
        double oldZ = newZ;

        if (newY != 0.0D) {
            int k = 0;
            for (int l = colliding.size(); k < l; ++k) {
                newY = colliding.get(k).calculateYOffset(boundingBox, newY);
            }

            boundingBox = boundingBox.offset(0.0D, newY, 0.0D);
        }

        if (newX != 0.0D) {
            int j5 = 0;

            for (int l5 = colliding.size(); j5 < l5; ++j5) {
                newX = colliding.get(j5).calculateXOffset(boundingBox, newX);
            }

            if (newX != 0.0D) {
                boundingBox = boundingBox.offset(newX, 0.0D, 0.0D);
            }
        }

        if (newZ != 0.0D) {
            int k5 = 0;

            for (int i6 = colliding.size(); k5 < i6; ++k5) {
                newZ = colliding.get(k5).calculateZOffset(boundingBox, newZ);
            }

            if (newZ != 0.0D) {
                boundingBox = boundingBox.offset(0.0D, 0.0D, newZ);
            }
        }

        this.x = (boundingBox.minX + boundingBox.maxX) / 2.0D;
        this.y = boundingBox.minY;
        this.z = (boundingBox.minZ + boundingBox.maxZ) / 2.0D;

        this.collidedVertically = oldY != newY;
        this.collidedHorizontally = oldX != newX || oldZ != newZ;
        this.collided = this.collidedHorizontally || this.collidedVertically;
        this.onGround = this.collidedVertically && oldY < 0.0D;

        if(oldX != newX) {
            motionX = 0.0D;
        }
        if(oldZ != newZ) {
            motionZ = 0.0D;
        }
        if(oldY != newY) {
            motionY = 0.0D;
        }
    }

    public void jump() {
        this.motionY = 0.42;
    }
}
