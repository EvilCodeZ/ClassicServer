package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.command.types.BlockArgumentType;
import de.evilcodez.classicserver.command.types.BlockPosArgumentType;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.World;
import de.evilcodez.classicserver.world.block.Block;

public class FillCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("fill").then(
                        argument("from", BlockPosArgumentType.blockPos()).then(
                                argument("to", BlockPosArgumentType.blockPos()).then(
                                        argument("block", BlockArgumentType.block()).executes(ctx -> {
                                            if(!ctx.getSource().isPlayer()) {
                                                notAPlayer(ctx);
                                                return 0;
                                            }
                                            if(!ctx.getSource().hasPermission("world.fill")) {
                                                noPermissions(ctx);
                                                return 0;
                                            }
                                            final BlockPos pos1 = ctx.getArgument("from", BlockPos.class);
                                            final BlockPos pos2 = ctx.getArgument("to", BlockPos.class);
                                            final Block block = ctx.getArgument("block", Block.class);
                                            final int minX = Math.min(pos1.getX(), pos2.getX());
                                            final int minY = Math.min(pos1.getY(), pos2.getY());
                                            final int minZ = Math.min(pos1.getZ(), pos2.getZ());
                                            final int maxX = Math.max(pos1.getX(), pos2.getX());
                                            final int maxY = Math.max(pos1.getY(), pos2.getY());
                                            final int maxZ = Math.max(pos1.getZ(), pos2.getZ());
                                            final World world = ctx.getSource().asPlayer().getWorld();
                                            for(int y = minY; y <= maxY; ++y) {
                                                for(int x = minX; x <= maxX; ++x) {
                                                    for(int z = minZ; z <= maxZ; ++z) {
                                                        world.updateBlock(x, y, z, block.getId());
                                                    }
                                                }
                                            }
                                            chatMessage(ctx, "&7Fill task completed!");
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
