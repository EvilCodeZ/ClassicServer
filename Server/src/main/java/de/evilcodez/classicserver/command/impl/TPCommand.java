package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.command.types.BlockPosArgumentType;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.utils.BlockPos;

import java.util.Collections;
import java.util.List;

public class TPCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("tp").then(
                        argument("player", StringArgumentType.word()).executes(ctx -> {
                            if(!ctx.getSource().isPlayer()) {
                                notAPlayer(ctx);
                                return 0;
                            }
                            if(!ctx.getSource().hasPermission("world.teleport.self")) {
                                noPermissions(ctx);
                                return 0;
                            }

                            final String name = ctx.getArgument("player", String.class);
                            final AbstractPlayer player = ctx.getSource().asPlayer().getWorld().getPlayers().stream()
                                    .filter(p -> p.getName().equalsIgnoreCase(name))
                                    .findFirst()
                                    .orElse(null);
                            if(player == null) {
                                chatMessage(ctx, "&cPlayer not found!");
                                return 0;
                            }
                            return teleport(ctx, Collections.singletonList(ctx.getSource().asPlayer()), player.x, player.y, player.z, player.yaw, player.pitch);
                        })
                ).then(
                        argument("destination", BlockPosArgumentType.blockPos()).executes(ctx -> {
                            if(!ctx.getSource().isPlayer()) {
                                notAPlayer(ctx);
                                return 0;
                            }
                            if(!ctx.getSource().hasPermission("world.teleport.self")) {
                                noPermissions(ctx);
                                return 0;
                            }

                            final BlockPos pos = ctx.getArgument("destination", BlockPos.class);
                            return teleport(ctx, Collections.singletonList(ctx.getSource().asPlayer()), pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
                        })
                )
        );
    }

    private static int teleport(CommandContext<ICommandSource> ctx, List<AbstractPlayer> players, double x, double y, double z, float yaw, float pitch) {
        for (AbstractPlayer player : players) {
            player.teleport(x, y, z, yaw, pitch);
        }
        return 1;
    }
}
