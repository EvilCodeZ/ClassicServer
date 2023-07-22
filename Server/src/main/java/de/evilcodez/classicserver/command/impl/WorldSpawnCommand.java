package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.World;

public class WorldSpawnCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("worldspawn").executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("world.worldspawn")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final World world = ctx.getSource().asPlayer().getWorld();
                    chatMessage(ctx, "&7World spawn: &6" + world.getSpawnPosition().getX() + " " + world.getSpawnPosition().getY() + " " + world.getSpawnPosition().getZ());
                    return 1;
                }).then(literal("set").executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("world.worldspawn.set")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final PlayerEntity player = ctx.getSource().asPlayer();
                    final World world = player.getWorld();
                    world.setSpawnPosition(new BlockPos((int) player.x, (int) player.y, (int) player.z));
                    MinecraftServer.getInstance().saveWorld(world);
                    chatMessage(ctx, "&7Successfully updated world spawn!");
                    return 1;
                }))
        );
    }
}
