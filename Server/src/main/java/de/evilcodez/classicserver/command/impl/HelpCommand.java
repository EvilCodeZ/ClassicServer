package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;

import java.util.Map;

public class HelpCommand extends Command {

    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(() -> "Unknown help command.");

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("help").executes(ctx -> {
                    final Map<CommandNode<ICommandSource>, String> map = dispatcher.getSmartUsage(dispatcher.getRoot(), ctx.getSource());
                    for (String usage : map.values()) {
                        ctx.getSource().sendChatMessage("&7/" + usage);
                    }
                    return map.size();
                }).then(
                        argument("command", StringArgumentType.word()).executes(ctx -> {
                            final ParseResults<ICommandSource> results = dispatcher.parse(ctx.getArgument("command", String.class), ctx.getSource());
                            if(results.getContext().getNodes().isEmpty()) {
                                throw FAILED_EXCEPTION.create();
                            }
                            final Map<CommandNode<ICommandSource>, String> map = dispatcher.getSmartUsage(results.getContext().getNodes().get(0).getNode(),
                                    ctx.getSource());
                            for (String usage : map.values()) {
                                ctx.getSource().sendChatMessage("&7/" + results.getReader().getString() + " " + usage);
                            }
                            return map.size();
                        })
                )
        );
    }
}
