package de.evilcodez.classicserver.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.evilcodez.classicserver.command.impl.*;

import java.util.function.Consumer;

public class CommandManager {

    private static boolean initialized;
    private static final CommandDispatcher<ICommandSource> dispatcher = new CommandDispatcher<>();

    public static void register(Consumer<CommandDispatcher<ICommandSource>> registerCallback) {
        if (initialized) {
            return;
        }
        initialized = true;
        HelpCommand.register(dispatcher);
        StopCommand.register(dispatcher);
        GotoCommand.register(dispatcher);
        WorldCommand.register(dispatcher);
        TpsCommand.register(dispatcher);
        LoginRegisterCommand.register(dispatcher);
        SetBlockCommand.register(dispatcher);
        SpawnNPCCommand.register(dispatcher);
        TPCommand.register(dispatcher);
        ListCommand.register(dispatcher);
        GameRuleCommand.register(dispatcher);
        FillCommand.register(dispatcher);
        PingCommand.register(dispatcher);
        OPCommand.register(dispatcher);
        KickCommand.register(dispatcher);
        WorldSpawnCommand.register(dispatcher);
        registerCallback.accept(dispatcher);
    }

    public static void execute(String command, ICommandSource source) {
        if(command.startsWith("/")) {
            command = command.substring(1);
        }
        try {
            final ParseResults<ICommandSource> results = dispatcher.parse(command, source);
            dispatcher.execute(results);
        } catch (CommandSyntaxException e) {
            String msg = "&7" + e.getRawMessage().getString();
            if(e.getInput() != null) {
                msg += "&8: &c/" + e.getInput() + (e.getContext() != null && e.getContext().startsWith("<--") ? e.getContext() : "");
            }
            Command.chatMessage(source, msg);
            if(e.getContext() != null && !e.getContext().startsWith("<--")) {
                Command.chatMessage(source, "&c" + e.getContext());
            }
        }catch (Throwable throwable) {
            source.sendChatMessage("&8[&cError&8] &7Failed to execute command: &6" + command);
            throwable.printStackTrace();
        }
    }
}
