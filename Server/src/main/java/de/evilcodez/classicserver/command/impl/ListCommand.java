package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("list").executes(ctx -> {
                    listPlayers(ctx, null);
                    return 1;
                }).then(argument("world", StringArgumentType.string()).executes(ctx -> {
                    listPlayers(ctx, ctx.getArgument("world", String.class));
                    return 1;
                }))
        );
    }

    private static void listPlayers(CommandContext<ICommandSource> ctx, String worldName) {
        final List<World> worlds = new ArrayList<>();
        if(worldName == null) worlds.addAll(MinecraftServer.getInstance().getWorlds());
        else Optional.ofNullable(MinecraftServer.getInstance().getWorld(worldName)).ifPresent(worlds::add);

        if(worlds.isEmpty()) {
            chatMessage(ctx, "&cWorld does not exist!");
            return;
        }

        for (World world : worlds) {
            chatMessage(ctx, "&6" + world.getName() + ": " + world.getPlayers().stream().filter(PlayerEntity.class::isInstance).count());
            for (AbstractPlayer player : world.getPlayers()) {
                if(player instanceof PlayerEntity) {
                    chatMessage(ctx, "&8- &7" + player.getName());
                }
            }
        }
    }
}
