package de.evilcodez.classicserver;

import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicserver.command.CommandManager;
import de.evilcodez.classicserver.network.ServerConnection;
import de.evilcodez.classicserver.option.ServerMessages;
import de.evilcodez.classicserver.option.ServerOptions;
import de.evilcodez.classicserver.permission.GroupManager;
import de.evilcodez.classicserver.permission.PlayerManager;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.player.ServerNetworkHandler;
import de.evilcodez.classicserver.plugin.Plugins;
import de.evilcodez.classicserver.utils.Timer;
import de.evilcodez.classicserver.utils.*;
import de.evilcodez.classicserver.utils.serverlist.BetacraftPing;
import de.evilcodez.classicserver.world.World;
import de.evilcodez.classicserver.world.WorldTransferTask;
import de.evilcodez.classicserver.world.gen.IWorldGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MinecraftServer {

    private static MinecraftServer instance;

    private final Plugins plugins;
    private ServerConnection serverConnection;
    private Thread serverThread;
    private Map<String, IWorldGenerator> worldGenerators;
    private List<ServerNetworkHandler> playerConnections;
    private Set<ProtocolExtension> supportedExtensions;
    private Queue<Runnable> taskQueue;
    private List<World> worlds;
    private World defaultWorld;
    private volatile boolean running;
    private int ticks;
    private TpsCounter tpsCounter;
    private ServerOptions serverOptions;
    private ServerMessages serverMessages;
    private PlayerManager playerManager;
    private GroupManager groupManager;
    private ConsoleCommandSource console;
    private WorldTransferTask worldTransferTask;
    private BetacraftPing betacraftPing;
    private long watchDogTimer;

    public MinecraftServer(Plugins plugins) {
        instance = this;
        this.plugins = plugins;
    }

    public void startServer() {
        this.running = true;
        final Timer timer = new Timer(20);
        this.serverThread = Thread.currentThread();
        this.worldGenerators = new HashMap<>();
        this.registerWorldGenerator("normal", IWorldGenerator.NORMAL_WORLD_GENERATOR);
        this.registerWorldGenerator("flat", IWorldGenerator.FLAT_WORLD_GENERATOR);
        this.taskQueue = new ConcurrentLinkedQueue<>();
        this.playerConnections = new CopyOnWriteArrayList<>();
        this.serverOptions = ServerOptions.loadOptions(new File("server_options.json"));
        this.serverMessages = ServerMessages.loadMessages(new File("messages.json"));
        this.playerManager = new PlayerManager();
        this.groupManager = new GroupManager();
        this.tpsCounter = new TpsCounter();
        this.registerExtensions();
        this.worldTransferTask = new WorldTransferTask(this.serverOptions.chunkPacketLimitPerTick);
        this.worldTransferTask.start();

        CommandManager.register(this.plugins::registerCommands);
        this.plugins.callServerStart(this);

        this.loadWorlds();
        try {
            this.serverConnection = new ServerConnection(serverOptions.serverIp, serverOptions.serverPort, 0);
            System.out.println("Successfully bound server.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server socket!", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveWorlds));
        if(this.serverOptions.betacraft.enabled) (this.betacraftPing = new BetacraftPing(this)).start();
        this.console = new ConsoleCommandSource();
        try {
            this.watchDogTimer = System.currentTimeMillis();
            this.startWatchDogThread();
            while (running) {
                timer.updateTimer();
                for (int j = 0; j < Math.min(10, timer.elapsedTicks); ++j) {
                    this.watchDogTimer = System.currentTimeMillis();
                    this.tick();
                }
                Thread.sleep(10L);
            }
        }catch (Throwable t) {
            System.err.println("!!!SERVER CRASHED!!!");
            t.printStackTrace();
        }
        worldTransferTask.running = false;
        serverConnection.close();
        System.exit(0);
    }

    private void tick() {
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().run();
        }
        for (World world : worlds) {
            world.tick();
        }
        if(this.defaultWorld == null || !this.defaultWorld.exists()) {
            this.generateDefaultWorld();
        }
        if(ticks % 3600 == 0) {
            this.saveWorlds();
        }
        tpsCounter.update();
        ++ticks;
    }

    private void startWatchDogThread() {
        final Thread thread = new Thread(() -> {
            while (running) {
                if(System.currentTimeMillis() - this.watchDogTimer > 60000L) {
                    System.err.println("!!!SERVER IS FREEZED!!!");
                    final ThreadDeath deathException = new ThreadDeath();
                    deathException.setStackTrace(serverThread.getStackTrace());
                    new RuntimeException("Server main thread is frozen!", deathException).printStackTrace();
                    this.shutdown();
                    return;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread.setName("WatchDog Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private void generateDefaultWorld() {
        final IWorldGenerator worldGen = this.getWorldGenerator(serverOptions.defaultWorldGenerator);
        if(worldGen == null) {
            throw new RuntimeException("World generator for default world not found!");
        }
        this.defaultWorld = World.generateWorld(serverOptions.defaultWorldName,
                serverOptions.defaultWorldSize.getX(),
                serverOptions.defaultWorldSize.getY(),
                serverOptions.defaultWorldSize.getZ(),
                worldGen);
        this.loadWorld(this.defaultWorld);
    }

    private void saveWorlds() {
        for (World world : worlds) {
            try {
                world.saveWorld(new File("worlds", world.getName() + ".lvl"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to save world: " + defaultWorld.getName(), e);
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public Plugins getPlugins() {
        return plugins;
    }

    public void registerWorldGenerator(String name, IWorldGenerator generator) {
        this.worldGenerators.put(name.toLowerCase(), generator);
    }

    public IWorldGenerator getWorldGenerator(String name) {
        return this.worldGenerators.get(name.toLowerCase());
    }

    public Map<String, IWorldGenerator> getWorldGenerators() {
        return worldGenerators;
    }

    public World getWorld(String name) {
        for(World world : this.worlds) {
            if(world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        return null;
    }

    public World getDefaultWorld() {
        return defaultWorld;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    private void loadWorlds() {
        this.worlds = new CopyOnWriteArrayList<>();

        final File dir = new File("worlds");
        if(dir.exists() && !dir.isDirectory()) {
            dir.delete();
        }
        if(!dir.exists()) {
            dir.mkdir();
            this.generateDefaultWorld();
            return;
        }
        final File[] files = dir.listFiles();
        for (File file : files) {
            if(!file.isFile()) {
                continue;
            }
            if(!file.getName().toLowerCase().endsWith(".lvl")) {
                continue;
            }
            final World world = new World(file.getName().substring(0, file.getName().length() - 4));
            try {
                System.out.println("Loading world " + world.getName() + "...");
                world.loadWorld(file);
                this.loadWorld(world);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load world: " + world.getName(), e);
            }
        }

        this.defaultWorld = this.getWorld("main");
        if(this.defaultWorld == null) {
            this.generateDefaultWorld();
        }
    }

    public String getServerName() {
        return serverOptions.serverName;
    }

    public String getServerMotd() {
        return serverOptions.serverMotds.isEmpty() ? "A Minecraft Classic Server"
                : serverOptions.serverMotds.get(ThreadLocalRandom.current().nextInt(serverOptions.serverMotds.size()));
    }

    public static MinecraftServer getInstance() {
        return instance;
    }

    public void loadWorld(World world) {
        final World oldWorld = this.getWorld(world.getName());
        if(oldWorld != null) {
            this.unloadWorld(oldWorld);
        }
        this.worlds.add(world);
        this.worlds.sort(Comparator.comparing(World::getName));
    }

    public void unloadWorld(World world) {
        worlds.remove(world);
        final List<AbstractPlayer> players = new ArrayList<>(world.getPlayers());
        if(this.worlds.isEmpty()) {
            for (AbstractPlayer player : players) {
                if(player instanceof PlayerEntity) {
                    ((PlayerEntity) player).getNetworkHandler().disconnect("&cFallback world not found!");
                }
            }
            return;
        }
        World fallback = this.getDefaultWorld().exists() ? this.getDefaultWorld() : this.worlds.get(0);
        for (AbstractPlayer player : players) {
            if(player instanceof PlayerEntity) {
                final PlayerEntity playerEntity = (PlayerEntity) player;
                final WorldEnterResult result;
                if ((result = playerEntity.getNetworkHandler().switchWorld(fallback)) != WorldEnterResult.SUCCESSFUL) {
                    playerEntity.getNetworkHandler().disconnect("&c" + result.getMessage());
                }
            }
        }
    }

    public void saveWorld(World world) {
        try {
            world.saveWorld(new File("worlds", world.getName() + ".lvl"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save world: " + (defaultWorld == null ? "null" : defaultWorld.getName()), e);
        }
    }

    public void execute(Runnable task) {
        this.taskQueue.add(task);
    }

    private void registerExtensions() {
        this.supportedExtensions = new HashSet<>();
        this.supportedExtensions.add(ProtocolExtension.TWO_WAY_PING);
        this.supportedExtensions.add(ProtocolExtension.BLOCK_PERMISSIONS);
        this.supportedExtensions.add(ProtocolExtension.BULK_BLOCK_UPDATE);
        this.supportedExtensions.add(ProtocolExtension.LONGER_MESSAGES);
        this.supportedExtensions.add(ProtocolExtension.HACK_CONTROL);
        this.supportedExtensions.add(ProtocolExtension.SET_SPAWNPOINT);
//        this.supportedExtensions.add(ProtocolExtension.EXT_ENTITY_POSITIONS);
    }

    public void broadcastMessage(PlayerEntity sender, String message) {
        System.out.println("[CHAT " + sender.getWorld().getName() + "] " + StringUtils.removeColorCodes(message));
        for (ServerNetworkHandler player : MinecraftServer.getInstance().getPlayerConnections()) {
            player.getPlayer().sendChatMessage(sender.getEntityId(), message);
        }
    }

    public void broadcastMessage(String message) {
        System.out.println("[CHAT] " + StringUtils.removeColorCodes(message));
        for (ServerNetworkHandler player : MinecraftServer.getInstance().getPlayerConnections()) {
            player.getPlayer().sendChatMessage(message);
        }
    }

    public void broadcastMessageExcept(String message, PlayerEntity exceptPlayer) {
        System.out.println("[CHAT] " + StringUtils.removeColorCodes(message));
        for (ServerNetworkHandler player : MinecraftServer.getInstance().getPlayerConnections()) {
            if(player.getPlayer() != exceptPlayer) {
                player.getPlayer().sendChatMessage(message);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPlayersOnline() {
        return playerConnections.size();
    }

    public ServerOptions getServerOptions() {
        return serverOptions;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public TpsCounter getTpsCounter() {
        return tpsCounter;
    }

    public int getRunningTicks() {
        return ticks;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public WorldTransferTask getWorldTransferTask() {
        return worldTransferTask;
    }

    public Set<ProtocolExtension> getSupportedExtensions() {
        return supportedExtensions;
    }

    public String getServerBrand() {
        return serverOptions.serverBrand;
    }

    public BetacraftPing getBetacraftPing() {
        return betacraftPing;
    }

    public List<ServerNetworkHandler> getPlayerConnections() {
        return playerConnections;
    }

    public int getMaxPlayers() {
        return Math.min(serverOptions.maxPlayers == -1 ? worlds.size() * 128 : serverOptions.maxPlayers, worlds.size() * 128);
    }

    public boolean isServerFull() {
        return playerConnections.size() >= this.getMaxPlayers();
    }

    public ServerMessages getServerMessages() {
        return serverMessages;
    }
}
