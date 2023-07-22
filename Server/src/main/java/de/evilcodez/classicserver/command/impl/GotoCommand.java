package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.ServerNetworkHandler;
import de.evilcodez.classicserver.utils.WorldEnterResult;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class GotoCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("goto").then(argument("world", StringArgumentType.greedyString()).executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("server.goto")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final World world = MinecraftServer.getInstance().getWorld(ctx.getArgument("world", String.class));
                    if(world == null) {
                        chatMessage(ctx, "&cWorld does not exist.");
                        chatMessage(ctx, "&7Worlds: &6" + MinecraftServer.getInstance().getWorlds().size());
                        for (World w : MinecraftServer.getInstance().getWorlds()) {
                            chatMessage(ctx, "&8- &6" + w.getName());
                        }
                        return 0;
                    }
                    if(world.equals(ctx.getSource().asPlayer().getWorld())) {
                        chatMessage(ctx, "&cYou are already in this world.");
                        return 0;
                    }
                    chatMessage(ctx, "&7Switching world...");
                    final WorldEnterResult result;
                    if((result = ctx.getSource().asPlayer().getNetworkHandler().switchWorld(world)) != WorldEnterResult.SUCCESSFUL) {
                        chatMessage(ctx, "&c" + result.getMessage());
                    }
                    return 1;
                })).executes(ctx -> {
                    chatMessage(ctx, "&7Worlds: &6" + MinecraftServer.getInstance().getWorlds().size());
                    for (World world : MinecraftServer.getInstance().getWorlds()) {
                        final String permission = world.getGameRule(GameRule.ENTER_WORLD);
                        final boolean canEnter = permission.isEmpty() || ctx.getSource().asPlayer().hasPermission(permission);
                        String colorCode;
                        if(!canEnter) colorCode = "&c";
                        else if(world.isWorldFull()) colorCode = "&4";
                        else colorCode = "&6";
                        chatMessage(ctx, "&8- " + colorCode + world.getName());
                    }
                    return 1;
                })
        );
        dispatcher.register(
                literal("send").then(argument("player", StringArgumentType.word()).then(argument("world", StringArgumentType.string()).executes(ctx -> {
                    if(!ctx.getSource().hasPermission("server.send")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final World world = MinecraftServer.getInstance().getWorld(ctx.getArgument("world", String.class));
                    if(world == null) {
                        chatMessage(ctx, "&cWorld does not exist.");
                        return 0;
                    }
                    final String playerName = ctx.getArgument("player", String.class);
                    final AtomicInteger counter = new AtomicInteger();
                    MinecraftServer.getInstance().getPlayerConnections().stream().map(ServerNetworkHandler::getPlayer)
                            .filter(p -> p != null && (playerName.equals("*") || p.getName().equalsIgnoreCase(playerName))).forEach(player -> {
                                counter.getAndIncrement();
                                if(world.equals(ctx.getSource().asPlayer().getWorld())) {
                                    chatMessage(ctx, "&cThe player is already in this world.");
                                    return;
                                }
                                final WorldEnterResult result;
                                if((result = player.getNetworkHandler().switchWorld(world, true)) != WorldEnterResult.SUCCESSFUL) {
                                    chatMessage(ctx, "&c" + result.getMessage());
                                    return;
                                }
                                chatMessage(ctx, "&7Sent player to world &6" + world.getName() + "&7.");
                            });
                    if(counter.get() == 0) {
                        chatMessage(ctx, "&cPlayer not online!");
                        return 0;
                    }
                    return 1;
                })))
        );
    }
}
