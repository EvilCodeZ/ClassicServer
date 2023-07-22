package de.evilcodez.classicserver.world;

import de.evilcodez.classicprotocol.packet.impl.Packet3LevelDataChunk;
import de.evilcodez.classicprotocol.packet.impl.Packet4LevelFinalize;
import de.evilcodez.classicserver.player.ServerNetworkHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldTransferTask extends Thread {

    private final int packetLimit;
    private final List<TaskEntry> tasks;
    private final Map<TaskEntry, CompletableFuture<ServerNetworkHandler>> futures;
    public boolean running;

    public WorldTransferTask(int packetLimit) {
        this.setName("World Transfer Thread");
        this.setDaemon(true);
        if(packetLimit < 1) {
            throw new IllegalArgumentException("Chunk packet limit must be greater than 0!");
        }
        this.packetLimit = packetLimit;
        this.tasks = new CopyOnWriteArrayList<>();
        this.futures = new HashMap<>();
    }

    @Override
    public synchronized void start() {
        super.start();
        this.running = true;
    }

    @Override
    public void run() {
        final List<TaskEntry> toRemove = new ArrayList<>();
        while(running) {
            if(tasks.isEmpty()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    return;
                }
                continue;
            }

            int chunkPacketCount = 0;
            final int packetsPerTask = Math.max(packetLimit / tasks.size(), 1);
            l: for (TaskEntry task : tasks) {

                for (int i = 0; i < packetsPerTask; i++) {
                    if(chunkPacketCount >= packetLimit) break l;
                    if(task.chunkQueue.isEmpty()) break;
                    final byte[] chunk = task.chunkQueue.poll();

                    task.networkHandler.sendPacketImmediately(new Packet3LevelDataChunk(chunk.length, chunk, (int) ((float) task.counter / (float) task.chunkCount * 100.0F)));
                    ++task.counter;
                    ++chunkPacketCount;
                }

                if(task.chunkQueue.isEmpty() || !task.networkHandler.getConnection().isConnected()) {
                    toRemove.add(task);
                    task.networkHandler.getPlayer().setLoadingTerrain(false);
                    final World world = task.networkHandler.getPlayer().getWorld();
                    task.networkHandler.sendPacketImmediately(new Packet4LevelFinalize(world.getLevel().sizeX, world.getLevel().sizeY, world.getLevel().sizeZ));

                    final CompletableFuture<ServerNetworkHandler> future = futures.remove(task);
                    if(!task.networkHandler.getConnection().isConnected()) {
                        future.completeExceptionally(new IOException("Connection lost!"));
                    }else {
                        future.complete(task.networkHandler);
                    }
                }
            }
            tasks.removeAll(toRemove);
            toRemove.clear();
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public CompletableFuture<ServerNetworkHandler> addTask(ServerNetworkHandler networkHandler, Queue<byte[]> chunks) {
        final CompletableFuture<ServerNetworkHandler> future = new CompletableFuture<>();
        final TaskEntry task = new TaskEntry(networkHandler, chunks);
        this.futures.put(task, future);
        this.tasks.add(task);
        return future;
    }

    private static class TaskEntry {

        public ServerNetworkHandler networkHandler;
        public Queue<byte[]> chunkQueue;
        public int chunkCount;
        public int counter;

        public TaskEntry(ServerNetworkHandler networkHandler, Queue<byte[]> chunkQueue) {
            this.networkHandler = networkHandler;
            this.chunkQueue = chunkQueue;
            this.chunkCount = chunkQueue.size();
        }
    }
}
