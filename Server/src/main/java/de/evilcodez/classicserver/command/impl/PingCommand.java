package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.evilcodez.classicprotocol.extension.ProtocolExtension;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.PlayerEntity;

public class PingCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("ping").executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("server.ping")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final PlayerEntity player = ctx.getSource().asPlayer();
                    if(player.getNetworkHandler().isExtensionEnabled(ProtocolExtension.TWO_WAY_PING)) {
                        chatMessage(ctx, "&7Ping: &6" + player.getNetworkHandler().getPing());
                    }else {
                        chatMessage(ctx, "&cYour client does not support ping!");
                    }
                    return 1;
                }).then(argument("player", StringArgumentType.word()).executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("server.ping.other")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final String name = ctx.getArgument("player", String.class);
                    final PlayerEntity player = (PlayerEntity) ctx.getSource().asPlayer().getWorld().getPlayers().stream()
                            .filter(p -> p instanceof PlayerEntity && p.getName().equalsIgnoreCase(name))
                            .findFirst()
                            .orElse(null);
                    if(player == null) {
                        chatMessage(ctx, "&cPlayer not found!");
                        return 0;
                    }
                    if(player.getNetworkHandler().isExtensionEnabled(ProtocolExtension.TWO_WAY_PING)) {
                        chatMessage(ctx, "&7" + player.getName() + "'s ping: &6" + player.getNetworkHandler().getPing());
                    }else {
                        chatMessage(ctx, "&c" + player.getName() + "'s client does not support ping!");
                    }
                    return 1;
                }))
        );
    }
}
