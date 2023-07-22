package de.evilcodez.classicserver.command;

import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.player.PlayerEntity;

public interface ICommandSource {

    void sendChatMessage(String message);

    boolean hasPermission(String permission);

    MinecraftServer getServer();

    PlayerEntity asPlayer();

    boolean isPlayer();

    String getName();
}
