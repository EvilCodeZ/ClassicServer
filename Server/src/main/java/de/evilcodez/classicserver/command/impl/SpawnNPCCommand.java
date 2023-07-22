package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.npc.PlayerNPC;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.utils.NameValidator;

public class SpawnNPCCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("spawnnpc").then(argument("name", StringArgumentType.word()).executes(ctx -> {
                    return spawnNPC(ctx, ctx.getArgument("name", String.class), 1);
                }).then(argument("count", IntegerArgumentType.integer(1, 127)).executes(ctx -> {
                    return spawnNPC(ctx, ctx.getArgument("name", String.class), ctx.getArgument("count", int.class));
                })))
        );

        dispatcher.register(
                literal("despawnnpc").then(argument("name", StringArgumentType.word()).executes(ctx -> {
                    if (!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("world.despawnnpc")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final PlayerEntity player = ctx.getSource().asPlayer();
                    final String name = ctx.getArgument("name", String.class);
                    if(!NameValidator.isValidUsername(name)) {
                        chatMessage(ctx, "&cNPC name is invalid!");
                        return 0;
                    }
                    player.getWorld().getPlayers().stream().filter(p -> p instanceof PlayerNPC && p.getName().equals(name)).forEach(p -> {
                        player.getWorld().despawnPlayer(p);
                        chatMessage(ctx, "&7Despawned " + p.getName() + " (" + p.getEntityId() + ")");
                    });
                    return 1;
                }))
        );
    }

    private static int spawnNPC(CommandContext<ICommandSource> ctx, String name, int count) {
        if (!ctx.getSource().isPlayer()) {
            notAPlayer(ctx);
            return 0;
        }
        if(!ctx.getSource().hasPermission("world.spawnnpc")) {
            noPermissions(ctx);
            return 0;
        }
        if(!NameValidator.isValidUsername(name)) {
            chatMessage(ctx, "&cNPC name is invalid!");
            return 0;
        }
        final PlayerEntity player = ctx.getSource().asPlayer();
        for (int i = 0; i < count; i++) {
            final PlayerNPC npc = player.getWorld().spawnPlayerNPC(name);
            if(npc == null) {
                chatMessage(ctx, "&cCan't spawn npc: World is full!");
                return 0;
            }
            npc.teleport(player.x, player.y - 1, player.z, player.yaw, player.pitch);
            chatMessage(ctx, "&7Spawned " + name + " (" + npc.getEntityId() + ")");
        }
        return 1;
    }
}
