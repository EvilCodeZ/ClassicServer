package de.evilcodez.classicserver.player;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.TcpConnection;
import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.PacketRegistry;
import de.evilcodez.classicprotocol.packet.impl.*;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet16ExtInfo;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet17ExtEntry;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet28SetBlockPermissions;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet43TwoWayPing;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.CommandManager;
import de.evilcodez.classicserver.network.FlushControlPacket;
import de.evilcodez.classicserver.permission.PermissionGroup;
import de.evilcodez.classicserver.permission.PlayerData;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.utils.NameValidator;
import de.evilcodez.classicserver.utils.StringUtils;
import de.evilcodez.classicserver.utils.WorldEnterResult;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;
import de.evilcodez.classicserver.world.block.Block;
import de.evilcodez.classicserver.world.block.BlockMaterial;
import de.evilcodez.classicserver.world.block.Blocks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ServerNetworkHandler implements IPacketHandler {

    private final Queue<IPacket> packetQueue;
    private final Set<ProtocolExtension> enabledExtensions;
    private TcpConnection connection;
    private boolean handshaked;
    private String username;
    private PlayerEntity player;
    private int tickCounter;
    private PlayerData playerData;
    private ThreadLocal<Boolean> flushPackets = ThreadLocal.withInitial(() -> true);

    // Offline mode auth
    public boolean authenticated;

    // Chat Spam Delay
    private int chatCounter;
    private long lastChatTime;

    // Block spam Delay
    private long lastBlockTime;

    // Protocol Extension
    private boolean extendedProtocol;
    private boolean extensionInfoReceived;
    private int expectedExtensionCount;
    private int extensionCounter;

    // LongerMessages
    private StringBuilder messageBuilder = new StringBuilder();

    // TwoWayPing
    private long twoWayTimer;
    private int twoWayId;
    private int ping;

    public ServerNetworkHandler() {
        this.packetQueue = new ArrayDeque<>();
        this.enabledExtensions = new HashSet<>();
    }

    public TcpConnection getConnection() {
        return connection;
    }

    public void setConnection(TcpConnection connection) {
        this.connection = connection;
    }

    public void disconnect(String reason) {
        this.sendPacketImmediately(new Packet14Disconnect(reason));
        try {
            Thread.sleep(2L);
        } catch (InterruptedException e) {}
        this.connection.disconnect();
    }

    public void sendPacket(IPacket packet) {
        if(player.isLoadingTerrain()) {
            this.packetQueue.add(packet);
            return;
        }
        this.sendPacketImmediately(packet);
    }

    public void sendPacketImmediately(IPacket packet) {
        if(packet instanceof FlushControlPacket) {
            final FlushControlPacket flushControl = (FlushControlPacket) packet;
            switch (flushControl.getMode()) {
                case ENABLE_FLUSH:
                    flushPackets.set(true);
                    break;
                case DISABLE_FLUSH:
                    flushPackets.set(false);
                    break;
                case FLUSH:
                    flushPackets.set(true);
                    connection.flush();
                    break;
                case ONLY_FLUSH:
                    connection.flush();
                    break;
            }
            return;
        }
        if(flushPackets.get()) {
            this.connection.sendPacket(packet);
        }else {
            this.connection.sendPacketNoFlush(packet);
        }
    }

    @Override
    public void onDisconnect(String reason) {
        if(player == null || !this.handshaked) {
            return;
        }
        MinecraftServer.getInstance().getPlayerConnections().remove(this);
        player.getWorld().despawnPlayer(player);
        if(connection.getDisconnectError() != null) {
            connection.getDisconnectError().printStackTrace();
        }
        System.out.println("[" + connection.getRemoteAddress() + "] " + player.getName() + " left the server: " + reason);
    }

    @Override
    public void handle(Packet0Identification packet) {
        if(this.handshaked) {
            connection.disconnect();
            return;
        }
        this.handshaked = true;

        // Check protocol version
        if(packet.getProtocolVersion() != PacketRegistry.PROTOCOL_VERSION) {
            if(packet.getProtocolVersion() < PacketRegistry.PROTOCOL_VERSION) {
                this.disconnect("Outdated client!");
                return;
            }
            this.disconnect("Outdated server!");
            return;
        }

        this.username = packet.getUsername();

        // Validate username & check username length
        if(!NameValidator.isValidUsername(packet.getUsername())) {
            this.disconnect("Invalid username!");
            return;
        }else if(packet.getUsername().length() > 16) {
            this.disconnect("Username is longer than 16 characters!");
            return;
        }

        // Check for username duplicates
        int playersWithSameIp = 0;
        for(ServerNetworkHandler player : MinecraftServer.getInstance().getPlayerConnections()) {
            if(player.getConnection().getRemoteAddress().getAddress().getHostAddress().equals(connection.getRemoteAddress().getAddress().getHostAddress())) {
                ++playersWithSameIp;
            }
            if(player.getPlayer().getName().equalsIgnoreCase(packet.getUsername())) {
                this.disconnect("&cA user with that name is already on the server!");
                return;
            }
        }
        final int maxPlayersPerIp = MinecraftServer.getInstance().getServerOptions().maxPlayersPerIp;
        if(maxPlayersPerIp > -1 && playersWithSameIp >= maxPlayersPerIp) {
            this.disconnect("&cToo many players are already connected with your IP!");
            return;
        }

        // Classic Protocol Extension
        if(packet.getPermissionLevel() == 0x42) {
            this.connection.getPacketRegistry().enableProtocolExtensions();
            this.extendedProtocol = true;
            final Set<ProtocolExtension> supported = MinecraftServer.getInstance().getSupportedExtensions();
            this.sendPacketImmediately(new Packet16ExtInfo(MinecraftServer.getInstance().getServerBrand(), supported.size()));
            for (ProtocolExtension extension : supported) {
                this.sendPacketImmediately(new Packet17ExtEntry(extension.getName(), extension.getHighestSupportedVersion()));
            }
            return;
        }

        this.loginPlayer();
    }

    private void loginPlayer() {
        // Spawn player
        if(MinecraftServer.getInstance().isServerFull()) {
            this.disconnect("&8[&6Warning&8] &cServer is full!");
            return;
        }
        final World world = MinecraftServer.getInstance().getDefaultWorld();
        if(world.isWorldFull()) {
            this.disconnect("&8[&6Warning&8] &cWorld is full!");
            return;
        }

        // Offline mode authentication
        this.playerData = MinecraftServer.getInstance().getPlayerManager().getPlayerData(this.username);
        final boolean isNewPlayer = this.playerData == null;
        if(playerData == null) {
            this.playerData = new PlayerData(
                    null,
                    MinecraftServer.getInstance().getGroupManager().getDefaultGroup().getName(),
                    connection.getRemoteAddress().getHostString()
            );
        }

        this.player = new PlayerEntity(world, this, world.getNextEntityId(), this.username,
                world.getSpawnPosition().getX() + 0.5, world.getSpawnPosition().getY() + 0.5, world.getSpawnPosition().getZ() + 0.5, 0.0F, 45.0F);
        this.player.setPermissionGroup(MinecraftServer.getInstance().getGroupManager().getGroupOrDefault(playerData.getPermissionGroup()));

        MinecraftServer.getInstance().getServerMessages().sendJoinMessages(this);
        final WorldEnterResult result;
        if((result = world.spawnPlayer(this.player, false)) != WorldEnterResult.SUCCESSFUL) {
            this.disconnect("&c" + result.getMessage());
            return;
        }
        this.sendBlockPermissions();

        authenticated = !MinecraftServer.getInstance().getServerOptions().offlineModeAuth;
        if(!isNewPlayer && playerData.getLastIpAddress().equals(connection.getRemoteAddress().getHostString())) {
            authenticated = true;
        }
        this.playerData.setLastIpAddress(connection.getRemoteAddress().getHostString());

        MinecraftServer.getInstance().getPlayerManager().savePlayerData(this.username, playerData);
        MinecraftServer.getInstance().getPlayerConnections().add(this);

        if(!authenticated) {
            if(playerData.getPasswordHash() == null) {
                this.player.sendChatMessage("&cUse /register <Password> <Password>");
            }else {
                this.player.sendChatMessage("&cUse /login <Password>");
            }
        }
    }

    @Override
    public void handle(Packet5SetBlock packet) {
        if(player == null) {
            connection.disconnect();
            return;
        }
        Block current = Blocks.blocks[player.getWorld().getLevel().getBlock(packet.getX(), packet.getY(), packet.getZ())];
        final long now = System.currentTimeMillis();
        final int threshold = player.getWorld().getBlockChangeThreshold();
        final boolean spamDetected = threshold > 0 && now - lastBlockTime <= threshold && MinecraftServer.getInstance().getServerOptions().blockSpamProtection;
        if(!authenticated || spamDetected) {
            this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
            return;
        }
        lastBlockTime = now;
        final String permission = player.getWorld().getGameRule(GameRule.WORLD_MODIFICATION);
        if(!permission.isEmpty() && !player.hasPermission(permission)) {
            this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
            return;
        }
        if(player.getWorld().isPositionInSpawnArea(new BlockPos(packet.getX(), packet.getY(), packet.getZ()))) {
            if (!player.hasPermission("world.modifyspawn") && !player.hasPermission("world.modifyspawn." + player.getWorld().getName().toLowerCase())) {
                this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
                this.player.sendChatMessage("&cYou don't have permission to modify the spawn area!");
                return;
            }
        }
        try {
            if(packet.isPlace()) {
                if(current.isSolid()) {
                    this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
                    return;
                }
                final Block block = Blocks.blocks[packet.getBlockId()];
                if (!Blocks.isPlaceable(block)) {
                    player.sendChatMessage("&8[&6Warning&8] &cYou can't place this block!");
                    this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
                    return;
                }
            }else if(current.getId() == Blocks.BEDROCK.getId()) {
                player.sendChatMessage("&8[&6Warning&8] &cYou can't break bedrock!");
                this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
                return;
            }else if(current.getMaterial() == BlockMaterial.FLUID) {
                this.sendPacket(new Packet6BlockChange(packet.getX(), packet.getY(), packet.getZ(), current.getId()));
                return;
            }
            final int maxBlockRange = player.getWorld().getBlockPlaceRange();
            MinecraftServer.getInstance().execute(() -> {
                if(maxBlockRange > 0 && player.distanceTo(packet.getX(), packet.getY(), packet.getZ()) >= maxBlockRange) {
                    this.disconnect("&cRange cheat!");
                    return;
                }
                player.getWorld().updateBlock(packet.getX(), packet.getY(), packet.getZ(), packet.isPlace() ? packet.getBlockId() : 0);
            });
        }catch (Exception ignored) {
            connection.disconnect();
        }
    }

    @Override
    public void handle(Packet8PositionRotation packet) {
        if(player == null) {
            connection.disconnect();
            return;
        }
        if(!authenticated) {
            this.sendPacket(new Packet8PositionRotation(255,
                    player.x, Math.floor(player.y), player.z, player.yaw, player.pitch));
            return;
        }
        if(player.isLoadingTerrain()) return;
        MinecraftServer.getInstance().execute(() -> {
            if(!player.checkMovement(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch())) return;

            player.updatePosition(packet.getX(), packet.getY(), packet.getZ());
            player.updateRotation(packet.getYaw(), packet.getPitch());
        });
    }

    @Override
    public void handle(Packet13Message packet) {
        if(player == null) {
            connection.disconnect();
            return;
        }
        if(this.isExtensionEnabled(ProtocolExtension.LONGER_MESSAGES)) {
            messageBuilder.append(packet.getMessage());
            if(messageBuilder.length() > 256) {
                messageBuilder = new StringBuilder();
                this.player.sendChatMessage("&cMessage too long!");
                return;
            }
            if(packet.getEntityId() == 0) {
                packet.setMessage(messageBuilder.toString());
                messageBuilder = new StringBuilder();
            }else {
                return;
            }
        }
        final String msg = StringUtils.removeColorCodes(packet.getMessage());
        if(msg.trim().isEmpty()) {
            return;
        }

        if(MinecraftServer.getInstance().getServerOptions().spamProtection) {
            ++chatCounter;
            if (chatCounter > 8) {
                this.disconnect("Chat spam!");
                return;
            } else if (System.currentTimeMillis() - lastChatTime < 250) {
                lastChatTime = System.currentTimeMillis();
                player.sendChatMessage("&8[&6Warning&8] &cPlease wait a moment before sending a message.");
                return;
            }
            if (System.currentTimeMillis() - lastChatTime >= 3000) {
                chatCounter = 0;
                lastChatTime = System.currentTimeMillis();
            }
        }

        if(!authenticated && !msg.startsWith("/register") && !msg.startsWith("/login")) {
            if(playerData.getPasswordHash() == null) {
                this.player.sendChatMessage("&cUse /register <Password> <Password>");
            }else {
                this.player.sendChatMessage("&cUse /login <Password>");
            }
            return;
        }

        if(msg.startsWith("/")) {
            if(player.isLoadingTerrain()) {
                this.player.sendChatMessage("&cWait until the world is loaded!");
                return;
            }
            MinecraftServer.getInstance().execute(() -> CommandManager.execute(msg, player));
            return;
        }
        MinecraftServer.getInstance().broadcastMessage(player, this.player.getDecoratedName("&6") + "&8: &7" + msg);
    }

    @Override
    public void handle(Packet16ExtInfo packet) {
        if(extensionInfoReceived) return;
        this.extensionInfoReceived = true;
        this.expectedExtensionCount = packet.getExtensionCount();
    }

    @Override
    public void handle(Packet17ExtEntry packet) {
        if(this.extensionCounter > expectedExtensionCount) {
            return;
        }
        ++this.extensionCounter;
        try {
            final ProtocolExtension extension = ProtocolExtension.byName(packet.getName());
            if (extension == null) return;
            if (extension.getHighestSupportedVersion() != packet.getVersion()) return;
            if (!MinecraftServer.getInstance().getSupportedExtensions().contains(extension)) return;
            this.enabledExtensions.add(extension);
            this.connection.getPacketRegistry().enableExtension(extension, packet.getVersion());
        }finally {
            if(this.extensionCounter == expectedExtensionCount) {
                this.loginPlayer();
            }
        }
    }

    @Override
    public void handle(Packet43TwoWayPing packet) {
        if(packet.getDirection() == 1 && this.twoWayId - 1 == packet.getData()) {
            this.ping = (int) (System.currentTimeMillis() - this.twoWayTimer);
            return;
        }
        this.sendPacketImmediately(new Packet43TwoWayPing(0, packet.getData()));
    }

    public void sendIdentification() {
        this.sendPacketImmediately(new Packet0Identification(
                PacketRegistry.PROTOCOL_VERSION,
                MinecraftServer.getInstance().getServerName(),
                MinecraftServer.getInstance().getServerMotd(),
                0
        ));
    }

    public void tick() {
        if(this.tickCounter % 20 == 0) {
            this.sendPacketImmediately(new Packet1Ping());
        }
        if(this.tickCounter % 60 == 0 && this.isExtensionEnabled(ProtocolExtension.TWO_WAY_PING)) {
            this.twoWayTimer = System.currentTimeMillis();
            this.sendPacketImmediately(new Packet43TwoWayPing(1, twoWayId++));
        }
        if(player != null) {
            if(!player.isLoadingTerrain()) {
                while (!packetQueue.isEmpty()) {
                    this.connection.sendPacket(packetQueue.poll());
                }
            }
        }
        ++this.tickCounter;
    }

    public WorldEnterResult switchWorld(World world) {
        return switchWorld(world, false);
    }

    public WorldEnterResult switchWorld(World world, boolean force) {
        if(world.isWorldFull()) {
            return WorldEnterResult.ERROR_WORLD_FULL;
        }
        if(!force) {
            final String permission = world.getGameRule(GameRule.ENTER_WORLD);
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                return WorldEnterResult.ERROR_NO_PERMISSIONS;
            }
        }
        final PlayerEntity player = new PlayerEntity(world, this, world.getNextEntityId(), this.player.getName(),
                world.getSpawnPosition().getX() + 0.5, world.getSpawnPosition().getY() + 0.5, world.getSpawnPosition().getZ() + 0.5, 0.0F, 45.0F);
        player.setPermissionGroup(MinecraftServer.getInstance().getGroupManager().getGroupOrDefault(playerData.getPermissionGroup()));
        this.player.getWorld().despawnPlayer(this.player);
        player.setLoadingTerrain(true);
        final WorldEnterResult result;
        if((result = world.spawnPlayer(player, force)) != WorldEnterResult.SUCCESSFUL) {
            this.disconnect("&c" + result.getMessage());
            return result;
        }
        this.player = player;
        this.sendBlockPermissions();
        MinecraftServer.getInstance().broadcastMessageExcept(this.player.getDecoratedName("&6") + "&7 went to world &3" + world.getName() + "&7.", this.player);
        return WorldEnterResult.SUCCESSFUL;
    }

    public void sendBlockPermissions() {
        if(this.isExtensionEnabled(ProtocolExtension.BLOCK_PERMISSIONS)) {
            final String permission = player.getWorld().getGameRule(GameRule.WORLD_MODIFICATION);
            final boolean allowModifications = permission.isEmpty() || player.hasPermission(permission);
            for (Block block : Blocks.blocks) {
                if(block.getId() == 0) continue;
                this.sendPacket(new Packet28SetBlockPermissions(block.getId(), allowModifications && Blocks.isPlaceable(block), allowModifications && Blocks.isBreakable(block)));
            }
        }
    }

    public boolean isExtensionEnabled(ProtocolExtension extension) {
        return enabledExtensions.contains(extension);
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public int getPing() {
        return ping;
    }

    public void changePermissionGroup(PermissionGroup group) {
        playerData.setPermissionGroup(group.getName());
        player.setPermissionGroup(group);
        MinecraftServer.getInstance().getPlayerManager().savePlayerData(this.username.toLowerCase(), playerData);
        this.sendBlockPermissions();
    }
}
