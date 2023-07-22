package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.player.ServerNetworkHandler;

public class KickCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("kick").then(argument("player", StringArgumentType.word()).executes(ctx -> {
                    if(!ctx.getSource().hasPermission("server.kick")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final PlayerEntity player = MinecraftServer.getInstance().getPlayerConnections().stream().map(ServerNetworkHandler::getPlayer)
                            .filter(p -> p != null && p.getName().equalsIgnoreCase(ctx.getArgument("player", String.class)))
                            .findFirst()
                            .orElse(null);
                    if(player == null) {
                        chatMessage(ctx, "&cPlayer not online!");
                        return 0;
                    }
                    kickPlayer(player.getNetworkHandler(), "&cYou have been kicked!");
                    return 1;
                }).then(argument("message", StringArgumentType.greedyString()).executes(ctx -> {
                    if(!ctx.getSource().hasPermission("server.kick")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final PlayerEntity player = MinecraftServer.getInstance().getPlayerConnections().stream().map(ServerNetworkHandler::getPlayer)
                            .filter(p -> p != null && p.getName().equalsIgnoreCase(ctx.getArgument("player", String.class)))
                            .findFirst()
                            .orElse(null);
                    if(player == null) {
                        chatMessage(ctx, "&cPlayer not online!");
                        return 0;
                    }
                    kickPlayer(player.getNetworkHandler(), ctx.getArgument("message", String.class));
                    return 1;
                })))
        );
    }

    private static void kickPlayer(ServerNetworkHandler player, String message) {
        player.disconnect(message);
    }
}
