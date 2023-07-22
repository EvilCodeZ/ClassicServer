package de.evilcodez.classicprotocol.packet;

import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicprotocol.packet.impl.*;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet16ExtInfo;
import de.evilcodez.classicprotocol.packet.impl.ext.Packet17ExtEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacketRegistry {

    public static final int PROTOCOL_VERSION = 7;

    private final Map<Integer, Class<? extends IPacket>> idToClass;
    private final Map<Class<? extends IPacket>, Integer> classToId;
    private final Set<ProtocolExtension> enabledExtensions;
    private boolean extensionProtocolEnabled;

    public int getPacketId(IPacket packet) {
        return classToId.get(packet.getClass());
    }

    public <T extends IPacket> T createPacket(int id) {
        try {
            return (T) this.idToClass.get(id).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public PacketRegistry() {
        this.idToClass = new HashMap<>();
        this.classToId = new HashMap<>();
        this.enabledExtensions = new HashSet<>();
        this.registerPacket(0, Packet0Identification.class);
        this.registerPacket(1, Packet1Ping.class);
        this.registerPacket(2, Packet2LevelInitialize.class);
        this.registerPacket(3, Packet3LevelDataChunk.class);
        this.registerPacket(4, Packet4LevelFinalize.class);
        this.registerPacket(5, Packet5SetBlock.class);
        this.registerPacket(6, Packet6BlockChange.class);
        this.registerPacket(7, Packet7SpawnPlayer.class);
        this.registerPacket(8, Packet8PositionRotation.class);
        this.registerPacket(9, Packet9RelPositionRotation.class);
        this.registerPacket(10, Packet10RelPosition.class);
        this.registerPacket(11, Packet11RelRotation.class);
        this.registerPacket(12, Packet12DespawnPlayer.class);
        this.registerPacket(13, Packet13Message.class);
        this.registerPacket(14, Packet14Disconnect.class);
        this.registerPacket(15, Packet15UpdatePlayerType.class);
    }

    public boolean enableExtension(ProtocolExtension extension, int version) {
        if(!this.extensionProtocolEnabled) return false;
        if(!extension.supportsVersion(version)) return false;
        if(extension.getExtensionRegistry() == null) return false;
        extension.getExtensionRegistry().registerPackets(this);
        this.enabledExtensions.add(extension);
        return true;
    }

    public void enableProtocolExtensions() {
        this.registerPacket(16, Packet16ExtInfo.class);
        this.registerPacket(17, Packet17ExtEntry.class);
        this.extensionProtocolEnabled = true;
    }

    public void registerPacket(int packetId, Class<? extends IPacket> packetClass) {
        this.idToClass.put(packetId, packetClass);
        this.classToId.put(packetClass, packetId);
    }

    public boolean isExtensionProtocolEnabled() {
        return extensionProtocolEnabled;
    }

    public Set<ProtocolExtension> getEnabledExtensions() {
        return enabledExtensions;
    }

    public boolean isExtensionEnabled(ProtocolExtension extension) {
        return this.enabledExtensions.contains(extension);
    }
}
