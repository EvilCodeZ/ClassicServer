package de.evilcodez.classicserver.player;

import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicprotocol.packet.impl.Packet13Message;
import de.evilcodez.classicprotocol.packet.impl.Packet7SpawnPlayer;
import de.evilcodez.classicprotocol.packet.impl.Packet8PositionRotation;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet32HackControl;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet46SetSpawnPoint;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.permission.PermissionGroup;
import de.evilcodez.classicserver.utils.AxisAlignedBB;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;
import de.evilcodez.classicserver.world.block.Block;
import de.evilcodez.classicserver.world.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerEntity extends AbstractPlayer implements ICommandSource {

    private final ServerNetworkHandler networkHandler;
    private PermissionGroup permissionGroup;
    private boolean loadingTerrain;
    public boolean allowCheats;
    public int movementChecksDisabledTicks;
    public int age;

    public PlayerEntity(World world, ServerNetworkHandler networkHandler, int entityId, String username, double x, double y, double z, float yaw, float pitch) {
        super(entityId, username, world);
        this.networkHandler = networkHandler;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getDecoratedName(String colorCode) {
        if(this.permissionGroup != null && this.permissionGroup.isPrefixVisible()) {
            return "&7[" + this.permissionGroup.getPrefixColorCode() + this.permissionGroup.getName() + "&7] " + colorCode + this.getName();
        }
        return colorCode + this.getName();
    }

    @Override
    public void tick() {
        networkHandler.tick();
        ++this.age;
        super.tick();
    }

    @Override
    public void sendChatMessage(String message) {
        this.sendChatMessage(-1, message);
    }

    public void sendChatMessage(int entityId, String message) {
        final List<String> list = new ArrayList<>();
        while(message.length() > 64) {
            list.add(message.substring(0, 64));
            message = message.substring(64);
        }
        if(!message.isEmpty()) {
            list.add(message);
        }
        if(networkHandler.isExtensionEnabled(ProtocolExtension.LONGER_MESSAGES)) {
            for (int i = 0; i < list.size(); i++) {
                networkHandler.sendPacketImmediately(new Packet13Message(i == list.size() - 1 ? 1 : 0, list.get(i)));
            }
        }else {
            for (String msg : list) {
                networkHandler.sendPacketImmediately(new Packet13Message(entityId, msg));
            }
        }
    }

    @Override
    public MinecraftServer getServer() {
        return MinecraftServer.getInstance();
    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch) {
        super.teleport(x, y, z, yaw, pitch);
        this.movementCheck = false;
        this.networkHandler.sendPacket(new Packet8PositionRotation(255, x, y, z, yaw, pitch));
    }

    public World getWorld() {
        return world;
    }

    public ServerNetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public boolean isLoadingTerrain() {
        return loadingTerrain;
    }

    public void setLoadingTerrain(boolean loadingTerrain) {
        this.loadingTerrain = loadingTerrain;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
        this.updateHackControl();
    }

    @Override
    public boolean hasPermission(String permission) {
        return permission.equals("player.name." + this.name.toLowerCase()) || permissionGroup.hasPermission(permission);
    }

    @Override
    public PlayerEntity asPlayer() {
        return this;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public boolean checkMovement(double x, double y, double z, float yaw, float pitch) {
        if(this.allowCheats || !this.movementCheck) return true;
        if(movementChecksDisabledTicks > 0) {
            --movementChecksDisabledTicks;
            return true;
        }

        final BlockPos spawn = this.world.getSpawnPosition();

        // Allow players to teleport to world spawn
        if((int) x == spawn.getX() && (int) y == spawn.getY() + 1 && (int) z == spawn.getZ()) {
            return true;
        }

        double motionX = x - this.x;
        double motionY = y - this.y;
        double motionZ = z - this.z;
        final double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);

        // 0,24407030237208294 ClassiCube max speed and 0.42 is jump height
        if(speed > 0.248 || motionY > 0.6) {
            this.networkHandler.sendPacket(new Packet8PositionRotation(255, this.x, this.y - 0.40625, this.z, yaw, pitch));
            return false;
        }

        if(this.distanceTo(x, y, z) > 6.0) {
            this.networkHandler.sendPacket(new Packet8PositionRotation(255, this.x, this.y, this.z, yaw, pitch));
            return false;
        }

        AxisAlignedBB boundingBox = new AxisAlignedBB(
                this.x - 0.25D, this.y - 1.57375, this.z - 0.25D,
                this.x + 0.25D, this.y + 0.18, this.z + 0.25D
        );
        final List<AxisAlignedBB> colliding = this.world.getLevel().getBlockCollisionBoxes(boundingBox.addCoord(motionX, motionY, motionZ));
        final Map<BlockPos, Block> blocks = this.world.getLevel().getBlocksInBB(boundingBox.addCoord(motionX, motionY, motionZ));

        double oldX = motionX;
        double oldY = motionY;
        double oldZ = motionZ;

        if (motionY != 0.0) {
            int k = 0;
            for (int l = colliding.size(); k < l; ++k) {
                motionY = colliding.get(k).calculateYOffset(boundingBox, motionY);
            }

            boundingBox = boundingBox.offset(0.0D, motionY, 0.0D);
        }

        if (motionX != 0.0D) {
            int j5 = 0;

            for (int l5 = colliding.size(); j5 < l5; ++j5) {
                motionX = colliding.get(j5).calculateXOffset(boundingBox, motionX);
            }

            if (motionX != 0.0D) {
                boundingBox = boundingBox.offset(motionX, 0.0D, 0.0D);
            }
        }

        if (motionZ != 0.0D) {
            int k5 = 0;

            for (int i6 = colliding.size(); k5 < i6; ++k5) {
                motionZ = colliding.get(k5).calculateZOffset(boundingBox, motionZ);
            }

            if (motionZ != 0.0D) {
                boundingBox = boundingBox.offset(0.0D, 0.0D, motionZ);
            }
        }

        if(motionX != oldX || motionY != oldY || motionZ != oldZ) {
            if(this.world.getLevel().isPositionInLevel(new BlockPos((int) x, (int) y, (int) z))) {
                final Block block = Blocks.blocks[this.world.getLevel().getBlock((int) (x + oldX), (int) (y + oldY) - 1, (int) (z + oldZ))];
                if((block == Blocks.SLAB || blocks.containsValue(Blocks.SLAB))) {
                    return true;
                }
            }
            this.networkHandler.sendPacket(new Packet8PositionRotation(255, this.x, this.y, this.z, yaw, pitch));
            this.movementCheck = true;
            return false;
        }
        return true;
    }

    public void updateHackControl() {
        this.allowCheats = world.<Boolean>getGameRule(GameRule.CHEATS_ENABLED)
                || (permissionGroup.hasPermission("world.cheats") || permissionGroup.hasPermission("world.cheats." + this.name.toLowerCase()));
        if(this.networkHandler.isExtensionEnabled(ProtocolExtension.HACK_CONTROL)) {
            this.networkHandler.sendPacket(new Packet32HackControl(
                    this.allowCheats,
                    this.allowCheats,
                    this.allowCheats,
                    true,
                    true,
                    -1
            ));
        }
    }

    public void updateWorldSpawn(BlockPos spawnPosition) {
        this.updateWorldSpawn(spawnPosition.getX() + 0.5, spawnPosition.getY() + 0.5, spawnPosition.getZ() + 0.5, 0.0f, 45.0f);
    }

    public void updateWorldSpawn(double x, double y, double z, float yaw, float pitch) {
        if(this.networkHandler.isExtensionEnabled(ProtocolExtension.SET_SPAWNPOINT)) {
            this.networkHandler.sendPacket(new Packet46SetSpawnPoint(x, y, z, yaw, pitch));
            return;
        }
        this.networkHandler.sendPacket(new Packet7SpawnPlayer(255, this.name, x, y, z, yaw, pitch));
        this.networkHandler.sendPacket(new Packet8PositionRotation(255, this.x, this.y, this.z, this.yaw, this.pitch));
    }
}
