package de.evilcodez.classicserver.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class Command {

    public static LiteralArgumentBuilder<ICommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<ICommandSource, T> argument(String name, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    public static void chatMessage(CommandContext<ICommandSource> ctx, String message) {
        chatMessage(ctx.getSource(), message);
    }

    public static void chatMessage(ICommandSource player, String message) {
        player.sendChatMessage("&8[&6Server&8] &f" + message);
    }

    public static void noPermissions(CommandContext<ICommandSource> ctx) {
        chatMessage(ctx, "&cYou don't have enough permissions to run &cthis &ccommand!");
    }

    public static void notAPlayer(CommandContext<ICommandSource> ctx) {
        chatMessage(ctx, "&cOnly a player can execute this &ccommand!");
    }
}
