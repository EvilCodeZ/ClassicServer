package de.evilcodez.classicprotocol.extension;

import de.evilcodez.classicprotocol.packet.PacketRegistry;

@FunctionalInterface
public interface ExtensionRegistry {

    void registerPackets(PacketRegistry registry);
}
