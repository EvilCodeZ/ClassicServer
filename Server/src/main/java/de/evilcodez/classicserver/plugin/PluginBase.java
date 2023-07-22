package de.evilcodez.classicserver.plugin;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.ICommandSource;
import net.lenni0451.classtransform.TransformerManager;

public abstract class PluginBase {

    Plugins plugins;
    PluginMeta meta;

    protected void onPluginLoad(TransformerManager transformerManager) {}

    protected void onServerStart(MinecraftServer server) {}

    protected void onRegisterCommands(CommandDispatcher<ICommandSource> dispatcher) {}

    protected final Plugins getPlugins() {
        return plugins;
    }

    protected final PluginMeta getMeta() {
        return meta;
    }
}
