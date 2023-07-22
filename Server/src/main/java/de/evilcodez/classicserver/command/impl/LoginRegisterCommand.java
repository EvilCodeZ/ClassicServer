package de.evilcodez.classicserver.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.Command;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.permission.PlayerData;

public class LoginRegisterCommand extends Command {

    public static void register(CommandDispatcher<ICommandSource> dispatcher) {
        dispatcher.register(
                literal("register")
                        .then(
                                argument("password", StringArgumentType.string())
                                        .then(
                                                argument("repeat", StringArgumentType.word()).executes(ctx -> {
                                                    if(!ctx.getSource().isPlayer()) {
                                                        notAPlayer(ctx);
                                                        return 0;
                                                    }
                                                    if(ctx.getSource().asPlayer().getNetworkHandler().authenticated) {
                                                        chatMessage(ctx, "&cAlready logged in!");
                                                        return 1;
                                                    }
                                                    final PlayerData playerData = ctx.getSource().asPlayer().getNetworkHandler().getPlayerData();
                                                    if(playerData.getPasswordHash() != null) {
                                                        chatMessage(ctx, "&cPlease use /login <Password>");
                                                        return 0;
                                                    }
                                                    final String pw = ctx.getArgument("password", String.class);
                                                    final String repeat = ctx.getArgument("repeat", String.class);
                                                    if(!pw.equals(repeat)) {
                                                        chatMessage(ctx, "&cThe first password does not match the second!");
                                                        return 0;
                                                    }
                                                    playerData.setPasswordHash(PlayerData.hashPassword(pw));
                                                    MinecraftServer.getInstance().getPlayerManager().savePlayerData(ctx.getSource().getName(), playerData);
                                                    ctx.getSource().asPlayer().getNetworkHandler().authenticated = true;
                                                    chatMessage(ctx, "&7You have registered successfully!");
                                                    return 1;
                                                })
                                        )
                        )
        );
        dispatcher.register(
                literal("login")
                        .then(
                                argument("password", StringArgumentType.string())
                                        .executes(ctx -> {
                                            if (!ctx.getSource().isPlayer()) {
                                                notAPlayer(ctx);
                                                return 0;
                                            }
                                            if(ctx.getSource().asPlayer().getNetworkHandler().authenticated) {
                                                chatMessage(ctx, "&cAlready logged in!");
                                                return 1;
                                            }
                                            final PlayerData playerData = ctx.getSource().asPlayer().getNetworkHandler().getPlayerData();
                                            if (playerData.getPasswordHash() == null) {
                                                chatMessage(ctx, "&cPlease use /register <Password> <Password>");
                                                return 0;
                                            }
                                            final String pw = ctx.getArgument("password", String.class);
                                            if(!PlayerData.hashPassword(pw).equals(playerData.getPasswordHash())) {
                                                chatMessage(ctx, "&cWrong password!");
                                                return 1;
                                            }
                                            ctx.getSource().asPlayer().getNetworkHandler().authenticated = true;
                                            chatMessage(ctx, "&7You have logged in successfully!");
                                            return 1;
                                        })
                        )
        );
    }
}
