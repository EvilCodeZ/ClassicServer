package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;

public class StopCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("stop").executes(ctx -> {
                    if(!ctx.getSource().hasPermission("server.stop")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    chatMessage(ctx, "&cStopping server...");
                    MinecraftServer.getInstance().shutdown();
                    return 1;
                })
        );
    }
}
