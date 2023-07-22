package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.command.types.BlockArgumentType;
import de.evilcodez.classicserver.command.types.BlockPosArgumentType;
import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.block.Block;

public class SetBlockCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("setblock")
                        .then(argument("position", BlockPosArgumentType.blockPos()).then(argument("block", BlockArgumentType.block()).executes(ctx -> {
                            if(!ctx.getSource().isPlayer()) {
                                notAPlayer(ctx);
                                return 0;
                            }
                            if(!ctx.getSource().hasPermission("world.setblock")) {
                                noPermissions(ctx);
                                return 0;
                            }

                            final BlockPos pos = ctx.getArgument("position", BlockPos.class);
                            final Block block = ctx.getArgument("block", Block.class);

                            if(ctx.getSource().asPlayer().getWorld().updateBlock(pos, block.getId())) {
                                chatMessage(ctx, "&7Successfully set &6block&8!");
                            }else {
                                chatMessage(ctx, "&cFailed to set &6block&8!");
                            }

                            return 1;
                        })))
        );
    }
}
