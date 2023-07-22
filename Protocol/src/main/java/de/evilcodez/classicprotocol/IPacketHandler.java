package de.evilcodez.classicprotocol;

import de.evilcodez.classicprotocol.packet.impl.*;
import de.evilcodez.classicprotocol.packet.impl.ext.*;

public interface IPacketHandler {

    default void onConnected() {}

    void onDisconnect(String reason);

    default void handle(Packet0Identification packet) {}

    default void handle(Packet1Ping packet) {}

    default void handle(Packet2LevelInitialize packet) {}

    default void handle(Packet3LevelDataChunk packet) {}

    default void handle(Packet4LevelFinalize packet) {}

    default void handle(Packet5SetBlock packet) {}

    default void handle(Packet6BlockChange packet) {}

    default void handle(Packet7SpawnPlayer packet) {}

    default void handle(Packet8PositionRotation packet) {}

    default void handle(Packet9RelPositionRotation packet) {}

    default void handle(Packet10RelPosition packet) {}

    default void handle(Packet11RelRotation packet) {}

    default void handle(Packet12DespawnPlayer packet) {}

    default void handle(Packet13Message packet) {}

    default void handle(Packet14Disconnect packet) {}

    default void handle(Packet15UpdatePlayerType packet) {}

    // Extensions

    default void handle(Packet16ExtInfo packet) {}

    default void handle(Packet17ExtEntry packet) {}

    default void handle(Packet28SetBlockPermissions packet) {}

    default void handle(Packet32HackControl packet) {}

    default void handle(Packet38BulkBlockUpdate packet) {}

    default void handle(Packet43TwoWayPing packet) {}

    default void handle(Packet46SetSpawnPoint packet) {}
}
