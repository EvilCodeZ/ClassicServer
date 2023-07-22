package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;

public class TpsCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("tps").executes(ctx -> {
                    if(!ctx.getSource().hasPermission("server.tps")) {
                        noPermissions(ctx);
                        return 0;
                    }
                    chatMessage(ctx, "&7TPS: &6"
                            + String.format("%.1f", MinecraftServer.getInstance().getTpsCounter().getTPS())
                            + "&8, &71m &6"
                            + String.format("%.1f", MinecraftServer.getInstance().getTpsCounter().getAverageTPS(60))
                            + "&8, &75m &6"
                            + String.format("%.1f", MinecraftServer.getInstance().getTpsCounter().getAverageTPS(300))
                    );
                    return 1;
                })
        );
    }
}
