package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.command.types.BlockPosArgumentType;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.utils.NameValidator;
import de.evilcodez.classicserver.world.World;
import de.evilcodez.classicserver.world.gen.IWorldGenerator;

import java.io.File;
import java.io.IOException;

public class WorldCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("world")
                        .then(
                                literal("create")
                                        .then(
                                                argument("name", StringArgumentType.string()).then(
                                                        argument("size", BlockPosArgumentType.blockPos())
                                                                .then(
                                                                        argument("generator", StringArgumentType.word())
                                                                                .executes(ctx -> {
                                                                                    final String name = ctx.getArgument("name", String.class);
                                                                                    final BlockPos pos = ctx.getArgument("size", BlockPos.class);
                                                                                    final String gen = ctx.getArgument("generator", String.class);
                                                                                    return executeCreate(ctx, name, pos, gen);
                                                                                })
                                                                )
                                                )
                                        )
                        )
                        .then(
                                literal("delete").then(argument("name", StringArgumentType.greedyString()).executes(ctx -> {
                                    if(!ctx.getSource().hasPermission("server.delete")) {
                                        noPermissions(ctx);
                                        return 0;
                                    }
                                    final String name = ctx.getArgument("name", String.class);
                                    final World world = MinecraftServer.getInstance().getWorld(name);
                                    if(world == null) {
                                        chatMessage(ctx, "&cWorld does not exist!");
                                        return 0;
                                    }
                                    MinecraftServer.getInstance().unloadWorld(world);
                                    new File("worlds", world.getName() + ".lvl.data").delete();
                                    if(new File("worlds", world.getName() + ".lvl").delete()) {
                                        chatMessage(ctx, "&7Successfully deleted world!");
                                    }else {
                                        chatMessage(ctx, "&cCould not delete world!");
                                    }
                                    return 1;
                                }))
                        )
                        .then(
                                literal("save").then(argument("name", StringArgumentType.greedyString()).executes(ctx -> {
                                    if(!ctx.getSource().hasPermission("server.save")) {
                                        noPermissions(ctx);
                                        return 0;
                                    }
                                    final String name = ctx.getArgument("name", String.class);
                                    final World world = MinecraftServer.getInstance().getWorld(name);
                                    if(world == null) {
                                        chatMessage(ctx, "&cWorld does not exist!");
                                        return 0;
                                    }
                                    try {
                                        world.saveWorld(new File("worlds", world.getName() + ".lvl"));
                                        chatMessage(ctx, "&7World saved!");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        chatMessage(ctx, "&cFailed to save world:");
                                        chatMessage(ctx, "&c" + e.getClass().getSimpleName() + " - &c" + e.getMessage());
                                    }
                                    return 1;
                                }))
                        )
        );
    }

    private static int executeCreate(CommandContext<ICommandSource> ctx, String name, BlockPos pos, String gen) {
        if (!ctx.getSource().hasPermission("world.create")) {
            noPermissions(ctx);
            return 0;
        }
        if(!NameValidator.isValidUsername(name)) {
            chatMessage(ctx, "&cInvalid world name!");
            return 0;
        }
        if(MinecraftServer.getInstance().getWorld(name) != null) {
            chatMessage(ctx, "&cWorld does already exist!");
            return 0;
        }
        if(pos.getX() <= 0 || pos.getY() <= 0 || pos.getZ() <= 0) {
            chatMessage(ctx, "&cThe world size x, y and z &cmust &cbe bigger than &60!");
            return 0;
        }
        final IWorldGenerator worldGenerator = ctx.getSource().getServer().getWorldGenerator(gen);
        if(worldGenerator == null) {
            chatMessage(ctx, "&cUnknown world generator! &cAvailable:");
            for (String generatorName : ctx.getSource().getServer().getWorldGenerators().keySet()) {
                chatMessage(ctx, "&8- &6" + generatorName);
            }
            return 0;
        }

        final Thread thread = new Thread(() -> {
            try {
                chatMessage(ctx, "&7Generating world &6" + name + "&7... This may take a &7while!");
                final World world = World.generateWorld(name, pos.getX(), pos.getY(), pos.getZ(), worldGenerator);
                MinecraftServer.getInstance().loadWorld(world);
                chatMessage(ctx, "&7Successfully generated world!");
            }catch (Exception e) {
                e.printStackTrace();
                chatMessage(ctx, "&cFailed to generate world: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }, "World Generation Thread");
        thread.setDaemon(true);
        thread.start();

        return 1;
    }
}
