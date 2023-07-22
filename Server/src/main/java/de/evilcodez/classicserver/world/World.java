package de.evilcodez.classicserver.world;

import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.impl.Packet12DespawnPlayer;
import de.evilcodez.classicprotocol.packet.impl.Packet2LevelInitialize;
import de.evilcodez.classicprotocol.packet.impl.Packet6BlockChange;
import de.evilcodez.classicprotocol.packet.impl.Packet7SpawnPlayer;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet38BulkBlockUpdate;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.network.FlushControlPacket;
import de.evilcodez.classicserver.npc.PlayerNPC;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.utils.StringUtils;
import de.evilcodez.classicserver.utils.WorldEnterResult;
import de.evilcodez.classicserver.world.block.Block;
import de.evilcodez.classicserver.world.block.Blocks;
import de.evilcodez.classicserver.world.gen.IWorldGenerator;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class World {

    private final List<AbstractPlayer> players;
    private final String name;
    private final Map<GameRule, Object> gameRules;
    private final List<Packet6BlockChange> blockUpdates;
    private Level level;
    private int entityIdCounter;
    private int worldTime;
    private int blockPlaceRange;
    private int blockChangeThreshold;

    // Spawn Position
    private BlockPos spawnPosition;

    // Extensions
    private boolean areBulkBlockExtUsersOnline;

    public World(String name) {
        this.name = name;
        this.players = new CopyOnWriteArrayList<>();
        this.gameRules = new HashMap<>();
        this.blockUpdates = new ArrayList<>(1024);
        for (GameRule gameRule : GameRule.values()) {
            this.gameRules.put(gameRule, gameRule.getDefaultValue());
        }
        this.blockPlaceRange = (int) GameRule.BLOCK_RANGE.getDefaultValue();
        this.blockChangeThreshold = (int) GameRule.BLOCK_CHANGE_THRESHOLD.getDefaultValue();
    }

    public void tick() {
        for (AbstractPlayer player : players) {
            if (player instanceof PlayerEntity) {
                ((PlayerEntity) player).getNetworkHandler().sendPacket(new FlushControlPacket(FlushControlPacket.FlushMode.DISABLE_FLUSH));
            }
        }
        for (AbstractPlayer player : players) {
            player.tick();
        }

        if(!this.blockUpdates.isEmpty()) {
            final List<IPacket> bulkBlockUpdates = new ArrayList<>();
            if(areBulkBlockExtUsersOnline) {
                int offset = 0;
                while(offset < this.blockUpdates.size()) {
                    final int size = Math.min(256, this.blockUpdates.size() - offset);
                    if(size >= 160) { // If it's less than 160, it's not worth it to send it as a bulk update
                        final Packet38BulkBlockUpdate packet = new Packet38BulkBlockUpdate(this.level.sizeX, this.level.sizeZ, this.blockUpdates, offset);
                        if(packet.getUpdates() > 0) {
                            bulkBlockUpdates.add(packet);
                        }
                    }else {
                        for(int i = offset; i < offset + size; i++) {
                            bulkBlockUpdates.add(this.blockUpdates.get(i));
                        }
                    }
                    offset += 256;
                }
            }
            for (AbstractPlayer player : players) {
                if(player instanceof PlayerEntity) {
                    final PlayerEntity playerEntity = (PlayerEntity) player;
                    if(playerEntity.getNetworkHandler().isExtensionEnabled(ProtocolExtension.BULK_BLOCK_UPDATE)) {
//                        System.out.println("Sending bulk block update to " + playerEntity.getName() + ": " + bulkBlockUpdates.size());
                        bulkBlockUpdates.forEach(playerEntity.getNetworkHandler()::sendPacket);
                    }else {
                        blockUpdates.forEach(playerEntity.getNetworkHandler()::sendPacket);
                    }
                }
            }
            bulkBlockUpdates.clear();
            this.blockUpdates.clear();
        }

        if(this.getGameRule(GameRule.BLOCK_TICKING)) {
            final List<BlockPos> posList = new ArrayList<>(level.getTickList());
            final List<Block> blockList = new ArrayList<>(posList.size());
            posList.forEach(p -> blockList.add(Blocks.blocks[level.getBlock(p.getX(), p.getY(), p.getZ())]));
            for (int i = 0; i < posList.size(); ++i) {
                final BlockPos pos = posList.get(i);
                final Block block = blockList.get(i);
                block.tick(this, pos);
            }
        }

        ++worldTime;

        for (AbstractPlayer player : players) {
            if (player instanceof PlayerEntity) {
                ((PlayerEntity) player).getNetworkHandler().sendPacket(new FlushControlPacket(FlushControlPacket.FlushMode.FLUSH));
            }
        }
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public AbstractPlayer getPlayer(int entityId) {
        for (AbstractPlayer player : players) {
            if(player.getEntityId() == entityId) {
                return player;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public boolean exists() {
        return new File("worlds", name + ".lvl").exists();
    }

    public int getBlockPlaceRange() {
        return blockPlaceRange;
    }

    public int getBlockChangeThreshold() {
        return blockChangeThreshold;
    }

    public void loadWorld(File file) throws IOException {
        final FileInputStream fIn = new FileInputStream(file);
        final DataInputStream in = new DataInputStream(new GZIPInputStream(fIn));

        if(in.readInt() != 656127880) {
            throw new RuntimeException("Not a classic world!");
        }
        in.readByte(); // Level format
        in.readUTF(); // World name
        in.readUTF(); // Creator
        in.readLong(); // Creation time
        final int sizeX = in.readShort();
        final int sizeZ = in.readShort();
        final int sizeY = in.readShort();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0) {
            baos.write(buf, 0, len);
        }

        this.level = new Level();
        this.level.setData(sizeX, sizeY, sizeZ, baos.toByteArray());
        this.calculateSpawnPosition();

        in.close();

        final File dataFile = new File(file.getPath() + ".data");
        if(dataFile.exists()) {
            final DataInputStream dataIn = new DataInputStream(new FileInputStream(dataFile));
            final int version = dataIn.readInt();
            // Read entities
            while((len = dataIn.readUnsignedByte()) > 0) {
                final char[] nameChars = new char[len];
                for (int i = 0; i < nameChars.length; i++) {
                    nameChars[i] = (char) dataIn.readUnsignedByte();
                }
                final String name = new String(nameChars);
                final double x = dataIn.readShort() / 32.0;
                final double y = dataIn.readShort() / 32.0;
                final double z = dataIn.readShort() / 32.0;
                final float yaw = dataIn.readByte() / 256.0f * 360.0f;
                final float pitch = dataIn.readByte() / 256.0f * 360.0f;
                final PlayerNPC npc = this.spawnPlayerNPC(name);
                npc.teleport(x, y, z, yaw, pitch);
            }

            if(dataIn.readBoolean()) {
                this.spawnPosition = new BlockPos(dataIn.readShort(), dataIn.readShort(), dataIn.readShort());
            }

            // Read game rules
            for (GameRule gameRule : GameRule.values()) {
                if(dataIn.available() < 1) {
                    System.out.println(this.name + " is missing game rule: " + gameRule.name());
                    continue;
                }
                Object value;
                if(gameRule.getDefaultValue() instanceof Boolean) {
                    value = dataIn.readBoolean();
                }else if(gameRule.getDefaultValue() instanceof String) {
                    value = dataIn.readUTF();
                }else if(gameRule.getDefaultValue() instanceof Integer) {
                    value = dataIn.readInt();
                }else {
                    throw new RuntimeException("Unknown game rule type: " + gameRule.getDefaultValue().getClass().getSimpleName());
                }
                this.setGameRule(gameRule, value);
            }

            dataIn.close();
        }
    }

    public void saveWorld(File file) throws IOException {
        final File tempFile = new File(file.getPath() + ".tmp");
        try (final FileOutputStream fOut = new FileOutputStream(tempFile)) {
            final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(fOut));

            out.writeInt(656127880); // magic
            out.write(1); // level format
            out.writeUTF(name); // name
            out.writeUTF("Classic Server"); // creator
            out.writeLong(System.currentTimeMillis()); // createTime
            out.writeShort(level.sizeX); // sizeX
            out.writeShort(level.sizeZ); // sizeZ
            out.writeShort(level.sizeY); // sizeY
            out.write(level.blocks); // blocks

            out.flush();
            out.close();

            file.delete();
            Files.move(tempFile.toPath(), file.toPath());
        }catch (IOException e) {
            tempFile.delete();
            throw e;
        }

        final File dataTempFile = new File(file.getPath() + ".data.tmp");
        try (final DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(dataTempFile))) {
            dataOut.writeInt(1); // Version

            // Write entities
            for (AbstractPlayer player : this.players) {
                if (player instanceof PlayerNPC) {
                    dataOut.writeByte(player.getName().length());
                    for (int i = 0; i < player.getName().length(); i++) {
                        dataOut.writeByte(player.getName().charAt(i));
                    }
                    dataOut.writeShort((int) (player.x * 32.0));
                    dataOut.writeShort((int) (player.y * 32.0));
                    dataOut.writeShort((int) (player.z * 32.0));
                    dataOut.writeByte((int) (player.yaw / 360.0f * 256.0f));
                    dataOut.writeByte((int) (player.pitch / 360.0f * 256.0f));
                }
            }
            dataOut.writeByte(0);

            dataOut.writeBoolean(spawnPosition != null);
            if(spawnPosition != null) {
                dataOut.writeShort(spawnPosition.getX());
                dataOut.writeShort(spawnPosition.getY());
                dataOut.writeShort(spawnPosition.getZ());
            }

            // Write game rules
            for (GameRule gameRule : GameRule.values()) {
                if (gameRule.getDefaultValue() instanceof Boolean) {
                    dataOut.writeBoolean(this.getGameRule(gameRule));
                } else if (gameRule.getDefaultValue() instanceof String) {
                    dataOut.writeUTF(this.getGameRule(gameRule));
                } else if (gameRule.getDefaultValue() instanceof Integer) {
                    dataOut.writeInt(this.getGameRule(gameRule));
                } else {
                    throw new RuntimeException("Unknown game rule type: " + gameRule.getDefaultValue().getClass().getSimpleName());
                }
            }

            dataOut.flush();
            dataOut.close();

            final File dataFile = new File(file.getPath() + ".data");
            dataFile.delete();
            Files.move(dataTempFile.toPath(), dataFile.toPath());
        }catch (IOException e) {
            dataTempFile.delete();
            throw e;
        }
    }

    public static World generateWorld(String name, int sizeX, int sizeY, int sizeZ, IWorldGenerator worldGenerator) {
        System.out.println("Generating world " + name + "...");
        long start = System.currentTimeMillis();
        final World world = new World(name);
        world.level = new Level();
        world.level.setSize(sizeX, sizeY, sizeZ);
        worldGenerator.generate(world.level);
        worldGenerator.populate(world.level);
        world.calculateSpawnPosition();
        MinecraftServer.getInstance().saveWorld(world);
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to generate world!");
        return world;
    }

    public PlayerNPC spawnPlayerNPC(String name) {
        if(this.isWorldFull()) return null;
        final PlayerNPC npc = new PlayerNPC(name, this, this.getNextEntityId());
        this.broadcastPacket(new Packet7SpawnPlayer(
                npc.getEntityId(),
                npc.getName(),
                npc.x, npc.y + 1.62, npc.z,
                npc.yaw, npc.pitch
        ));
        this.players.add(npc);
        return npc;
    }

    public void despawnPlayer(AbstractPlayer player) {
        if(player instanceof PlayerEntity) {
            boolean areBulkBlockExtUsersOnline = false;
            for (AbstractPlayer p : this.players) {
                if (p.equals(player) && !((PlayerEntity) player).getNetworkHandler().getConnection().isConnected()) {
                    continue;
                }
                if (p instanceof PlayerEntity) {
                    final PlayerEntity playerEntity = (PlayerEntity) p;
                    playerEntity.getNetworkHandler().sendPacketImmediately(new Packet12DespawnPlayer(player.getEntityId()));
                    if (playerEntity.getNetworkHandler().isExtensionEnabled(ProtocolExtension.BULK_BLOCK_UPDATE)) {
                        areBulkBlockExtUsersOnline = true;
                    }
                }
            }
            this.areBulkBlockExtUsersOnline = areBulkBlockExtUsersOnline;
            this.players.remove(player);
            this.broadcastMessageExcept(((PlayerEntity) player).getDecoratedName("&6") + " &eleft the world.", (PlayerEntity) player);
            return;
        }
        this.players.remove(player);
        this.broadcastPacket(new Packet12DespawnPlayer(player.getEntityId()));
    }

    public WorldEnterResult spawnPlayer(PlayerEntity player, boolean force) {
        if(this.isWorldFull()) {
            return WorldEnterResult.ERROR_WORLD_FULL;
        }
        if(!force) {
            final String permission = this.getGameRule(GameRule.ENTER_WORLD);
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                return WorldEnterResult.ERROR_NO_PERMISSIONS;
            }
        }

        player.setLoadingTerrain(true);
        this.players.add(player);
        player.getNetworkHandler().sendIdentification();
        try {
            final byte[] compressed = Level.compress(level);
            player.getNetworkHandler().sendPacketImmediately(new Packet2LevelInitialize());
            final Queue<byte[]> chunkQueue = new ArrayDeque<>();
            int offset = 0;
            while (offset < compressed.length) {
                int size = Math.min(1024, compressed.length - offset);
                if (size <= 0) {
                    break;
                }
                final byte[] chunk = new byte[size];
                System.arraycopy(compressed, offset, chunk, 0, size);
                chunkQueue.add(chunk);
                offset += size;
            }
            MinecraftServer.getInstance().getWorldTransferTask().addTask(player.getNetworkHandler(), chunkQueue).thenAccept(client -> {
                try {
                    player.teleport(spawnPosition.getX() + 0.5, spawnPosition.getY() + 0.62, spawnPosition.getZ() + 0.5, 0.0F, 0.0F);
                    boolean areBulkBlockExtUsersOnline = false;
                    for (AbstractPlayer p : this.players) {
                        if (p == player) {
                            continue;
                        }

                        if(p instanceof PlayerEntity) {
                            final PlayerEntity playerEntity = (PlayerEntity) p;
                            playerEntity.getNetworkHandler().sendPacket(new Packet7SpawnPlayer(
                                    player.getEntityId(),
                                    player.getName(),
                                    player.x, player.y, player.z,
                                    player.yaw, player.pitch
                            ));
                            if(playerEntity.getNetworkHandler().isExtensionEnabled(ProtocolExtension.BULK_BLOCK_UPDATE)) {
                                areBulkBlockExtUsersOnline = true;
                            }
                        }

                        player.getNetworkHandler().sendPacket(new Packet7SpawnPlayer(
                                p.getEntityId(),
                                p.getName(),
                                p.x, p.y + (p instanceof PlayerNPC ? 1.62 : 0.0), p.z,
                                p.yaw, p.pitch
                        ));
                    }
                    player.getNetworkHandler().sendPacket(new Packet7SpawnPlayer(255,
                            player.getName(),
                            spawnPosition.getX() + 0.5, spawnPosition.getY() + 0.62, spawnPosition.getZ() + 0.5,
                            0.0f, 0.0f));
                    player.teleport(spawnPosition.getX() + 0.5, spawnPosition.getY() + 0.62, spawnPosition.getZ() + 0.5, 0.0F, 0.0F);
                    player.movementChecksDisabledTicks = 10;
                    if(player.getNetworkHandler().isExtensionEnabled(ProtocolExtension.BULK_BLOCK_UPDATE)) {
                        areBulkBlockExtUsersOnline = true;
                    }
                    this.areBulkBlockExtUsersOnline = areBulkBlockExtUsersOnline;

                    this.broadcastMessageExcept(player.getDecoratedName("&6") + " &ejoined the world.", player);
                    System.out.println(player.getName() + " entity id is " + player.getEntityId() + ".");

                    MinecraftServer.getInstance().getServerMessages().sendWorldMessages(player.getNetworkHandler(), this);
                    player.updateHackControl();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return WorldEnterResult.SUCCESSFUL;
    }

    public boolean updateBlock(BlockPos pos, int blockId) {
        if(!level.setBlock(pos.getX(), pos.getY(), pos.getZ(), blockId)) {
            return false;
        }
        final Packet6BlockChange packet = new Packet6BlockChange(pos.getX(), pos.getY(), pos.getZ(), blockId);
        if(areBulkBlockExtUsersOnline) {
            this.blockUpdates.add(packet);
        }else {
            this.broadcastPacket(packet);
        }
        return true;
    }

    public boolean updateBlock(int x, int y, int z, int blockId) {
        if(!level.setBlock(x, y, z, blockId)) {
            return false;
        }
        final Packet6BlockChange packet = new Packet6BlockChange(x, y, z, blockId);
        if(areBulkBlockExtUsersOnline) {
            this.blockUpdates.add(packet);
        }else {
            this.broadcastPacket(packet);
        }
        return true;
    }

    public boolean isPositionInSpawnArea(BlockPos position) {
        int range = this.getGameRule(GameRule.SPAWN_PROTECTION);
        if(range <= 0) return false;
        range /= 2;
        return position.getX() >= spawnPosition.getX() - range && position.getX() <= spawnPosition.getX() + range &&
                position.getZ() >= spawnPosition.getZ() - range && position.getZ() <= spawnPosition.getZ() + range;
    }

    public void broadcastMessage(int entityId, String message) {
        System.out.println("[CHAT " + this.name + "] " + StringUtils.removeColorCodes(message));
        for (AbstractPlayer player : players) {
            if(player instanceof PlayerEntity) {
                ((PlayerEntity) player).sendChatMessage(entityId, message);
            }
        }
    }

    public void broadcastMessage(String message) {
        System.out.println("[CHAT] " + StringUtils.removeColorCodes(message));
        for (AbstractPlayer player : players) {
            if(player instanceof PlayerEntity) {
                ((PlayerEntity) player).sendChatMessage(message);
            }
        }
    }

    public void broadcastMessageExcept(String message, PlayerEntity exceptPlayer) {
        System.out.println("[CHAT " + this.name + "] " + StringUtils.removeColorCodes(message));
        for (AbstractPlayer player : players) {
            if(player instanceof PlayerEntity && player != exceptPlayer) {
                ((PlayerEntity) player).sendChatMessage(message);
            }
        }
    }

    public void broadcastPacket(IPacket packet) {
        for (AbstractPlayer player : players) {
            if(player instanceof PlayerEntity) {
                final PlayerEntity playerEntity = (PlayerEntity) player;
                playerEntity.getNetworkHandler().sendPacket(packet);
            }
        }
    }

    public boolean isWorldFull() {
        return players.size() > 127;
    }

    public int getNextEntityId() {
        int entityId;
        do {
            entityId = entityIdCounter++;
            if(entityIdCounter > 127) {
                entityIdCounter = 0;
            }
        }while(this.getPlayer(entityId) != null);
        return entityId;
    }

    private void calculateSpawnPosition() {
        int spawnX = level.sizeX / 2;
        int spawnZ = level.sizeZ / 2;
        int spawnY = level.sizeY;
        while(spawnY > 0 && level.getBlock(spawnX, spawnY, spawnZ) == 0) {
            spawnY--;
        }
        ++spawnY;
        this.spawnPosition = new BlockPos(spawnX, spawnY + 1, spawnZ);
    }

    public BlockPos getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(BlockPos spawnPosition) {
        this.spawnPosition = spawnPosition;
        for (AbstractPlayer player : getPlayers()) {
            if(player instanceof PlayerEntity) {
                ((PlayerEntity) player).updateWorldSpawn(spawnPosition);
            }
        }
    }

    public int getWorldTime() {
        return worldTime;
    }

    public Map<GameRule, Object> getGameRules() {
        return gameRules;
    }

    public <T> T getGameRule(GameRule gameRule) {
        return (T) gameRules.getOrDefault(gameRule, gameRule.getDefaultValue());
    }

    public void setGameRule(GameRule gameRule, Object value) {
        if(!gameRule.getDefaultValue().getClass().isInstance(value)) {
            throw new RuntimeException("Invalid game rule value type " + value.getClass().getSimpleName() + ", expected " + gameRule.getDefaultValue().getClass().getSimpleName());
        }
        gameRules.put(gameRule, value);
        if (gameRule == GameRule.WORLD_MODIFICATION) {
            // Update block permissions for players
            for (AbstractPlayer player : getPlayers()) {
                if(player instanceof PlayerEntity) {
                    ((PlayerEntity) player).getNetworkHandler().sendBlockPermissions();
                }
            }
        }else if(gameRule == GameRule.CHEATS_ENABLED) {
            for (AbstractPlayer player : getPlayers()) {
                if(player instanceof PlayerEntity) {
                    ((PlayerEntity) player).updateHackControl();
                }
            }
        }else if(gameRule == GameRule.BLOCK_RANGE) {
            this.blockPlaceRange = (int) value;
        }else if(gameRule == GameRule.BLOCK_CHANGE_THRESHOLD) {
            this.blockChangeThreshold = (int) value;
        }
    }
}
