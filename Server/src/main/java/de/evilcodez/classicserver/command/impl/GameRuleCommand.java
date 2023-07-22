package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;

public class GameRuleCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("gamerule").then(
                        argument("name", StringArgumentType.word())
                                .then(argument("boolean", BoolArgumentType.bool()).executes(ctx -> {
                                    changeGameRule(ctx, ctx.getArgument("name", String.class), ctx.getArgument("boolean", boolean.class));
                                    return 1;
                                })).then(argument("number", IntegerArgumentType.integer()).executes(ctx -> {
                                    changeGameRule(ctx, ctx.getArgument("name", String.class), IntegerArgumentType.getInteger(ctx, "number"));
                                    return 1;
                                })).then(argument("string", StringArgumentType.string()).executes(ctx -> {
                                    changeGameRule(ctx, ctx.getArgument("name", String.class), ctx.getArgument("string", String.class));
                                    return 1;
                                }))
                ).executes(ctx -> {
                    if(!ctx.getSource().isPlayer()) {
                        notAPlayer(ctx);
                        return 0;
                    }
                    if(!ctx.getSource().hasPermission("world.gamerule")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    final World world = ctx.getSource().asPlayer().getWorld();
                    chatMessage(ctx, "&7Game rules:");
                    for (GameRule gameRule : GameRule.values()) {
                        chatMessage(ctx, "&8- &7" + gameRule.name().toLowerCase() + ": &6" + world.getGameRule(gameRule));
                    }
                    return 1;
                })
        );
    }

    private static void changeGameRule(CommandContext<ICommandSource> ctx, String gameRule, Object value) {
        if(!ctx.getSource().isPlayer()) {
            notAPlayer(ctx);
            return;
        }
        if(!ctx.getSource().hasPermission("world.gamerule.set")) {
            noPermissions(ctx);
            return;
        }
        GameRule rule = null;
        for (GameRule rule1 : GameRule.values()) {
            if(rule1.name().equalsIgnoreCase(gameRule)) {
                rule = rule1;
                break;
            }
        }
        if(rule == null) {
            chatMessage(ctx, "&cGame rule does not exist!");
            return;
        }
        final Object oldValue = ctx.getSource().asPlayer().getWorld().getGameRule(rule);
        if(oldValue.getClass() != value.getClass()) {
            chatMessage(ctx, "&cValue must be of type " + oldValue.getClass().getSimpleName() + ".");
            return;
        }
        try {
            ctx.getSource().asPlayer().getWorld().setGameRule(rule, value);
            chatMessage(ctx, "&7Game rule changed!");
        }catch(Exception e) {
            chatMessage(ctx, "&cFailed to change game rule:");
            chatMessage(ctx, e.getMessage());
        }
    }
}
