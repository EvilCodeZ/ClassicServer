package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.permission.PlayerData;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.player.ServerNetworkHandler;

import java.util.Locale;

public class OPCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("op").then(argument("player", StringArgumentType.word()).executes(ctx -> {
                    return execute(ctx, false);
                }))
        );
        dispatcher.register(
                literal("deop").then(argument("player", StringArgumentType.word()).executes(ctx -> {
                    return execute(ctx, true);
                }))
        );
    }

    private static int execute(CommandContext<ICommandSource> ctx, boolean deOp) {
        if(!ctx.getSource().hasPermission("server.op")) {
            noPermissions(ctx);
            return 0;
        }
        final String name = ctx.getArgument("player", String.class);
        final PlayerEntity player = (PlayerEntity) MinecraftServer.getInstance().getPlayerConnections().stream().map(ServerNetworkHandler::getPlayer)
                .filter(p -> p != null && p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if(deOp) {
            if(player != null) {
                player.getNetworkHandler().changePermissionGroup(MinecraftServer.getInstance().getGroupManager().getDefaultGroup());
                chatMessage(player, "&aYou are no longer an operator!");
            }else {
                final PlayerData playerData = MinecraftServer.getInstance().getPlayerManager().getPlayerData(name);
                if(playerData == null) {
                    chatMessage(ctx, "&cPlayer is not an operator!");
                    return 0;
                }
                playerData.setPermissionGroup(MinecraftServer.getInstance().getGroupManager().getDefaultGroup().getName());
                MinecraftServer.getInstance().getPlayerManager().savePlayerData(name.toLowerCase(), playerData);
            }
            chatMessage(ctx, "&aPlayer " + name + " is no longer an operator!");
        }else {
            if(player == null) {
                chatMessage(ctx, "&cPlayer not found!");
                return 0;
            }
            player.getNetworkHandler().changePermissionGroup(MinecraftServer.getInstance().getGroupManager().getAdministratorGroup());
            chatMessage(ctx, "&aPlayer " + player.getName() + " is now an operator!");
            chatMessage(player, "&aYou are now an operator!");
        }
        return 1;
    }
}
