package de.evilcodez.classicserver.plugin;

import com.mojang.brigadier.CommandDispatcher;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.ICommandSource;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.utils.tree.BasicClassProvider;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.*;
import java.util.function.Supplier;

public class Plugins {

    private final List<File> pluginsToLoad = new LinkedList<>();
    private final Map<PluginBase, PluginClassLoader> loadedPlugins = new IdentityHashMap<>();
    private boolean loaded;
    private boolean loadedCommands;
    private boolean calledServerStart;

    public void addPlugin(File file) {
        if (this.loaded) {
            throw new IllegalStateException("Plugins already loaded");
        }
        this.pluginsToLoad.add(file);
    }

    public void loadPlugins(Supplier<Instrumentation> instrumentationSupplier) {
        if (this.loaded) {
            throw new IllegalStateException("Plugins already loaded");
        }
        this.loaded = true;
        final List<TransformerManager> transformerManagers = new LinkedList<>();
        for (File file : this.pluginsToLoad) {
            if (!file.isFile()) continue;
            final PluginClassLoader classLoader = new PluginClassLoader(Thread.currentThread().getContextClassLoader());
            try {
                classLoader.addURL(file.toURI().toURL());
                try (final InputStream metaInputStream = classLoader.getResourceAsStream("plugin.properties")) {
                    if (metaInputStream == null) {
                        System.err.println("Plugin '" + file.getPath() + "' has no plugin.properties file");
                        continue;
                    }
                    final Properties properties = new Properties();
                    properties.load(metaInputStream);
                    if (properties.getProperty("name") == null) {
                        System.err.println("Plugin '" + file.getPath() + "' has no name defined");
                        continue;
                    }
                    if (properties.getProperty("main") == null) {
                        System.err.println("Plugin '" + file.getPath() + "' has no main class defined");
                        continue;
                    }
                    final PluginMeta meta = new PluginMeta(
                            properties.getProperty("name"),
                            properties.getProperty("main"),
                            properties.getProperty("version", "0.0.0"),
                            properties.getProperty("authors", "").split("( )*,( )*"),
                            properties.getProperty("description", ""),
                            Boolean.parseBoolean(properties.getProperty("cantransform", "false"))
                    );
                    final Class<?> mainClass = Class.forName(meta.getMainClass(), false, classLoader);
                    if (!PluginBase.class.isAssignableFrom(mainClass)) {
                        System.err.println("Plugin '" + file.getPath() + "' main class does not extend PluginBase");
                        continue;
                    }
                    final PluginBase plugin = (PluginBase) mainClass.newInstance();
                    plugin.meta = meta;
                    plugin.plugins = this;
                    final TransformerManager transformerManager = new TransformerManager(new BasicClassProvider(classLoader));
                    plugin.onPluginLoad(transformerManager);
                    this.loadedPlugins.put(plugin, classLoader);
                    if (meta.canTransform()) {
                        transformerManagers.add(transformerManager);
                    }
                    System.out.println("Loaded plugin: " + meta.getName());
                }
            } catch (Throwable throwable) {
                System.err.println("Failed to load plugin: " + file.getPath());
                throwable.printStackTrace();
            }
        }
        if (!transformerManagers.isEmpty()) {
            final Instrumentation instrumentation = instrumentationSupplier.get();
            for (TransformerManager transformerManager : transformerManagers) {
                transformerManager.hookInstrumentation(instrumentation);
            }
        }
    }

    public void registerCommands(CommandDispatcher<ICommandSource> dispatcher) {
        if (this.loadedCommands) {
            throw new IllegalStateException("Commands already registered");
        }
        this.loadedCommands = true;
        for (PluginBase plugin : this.loadedPlugins.keySet()) {
            plugin.onRegisterCommands(dispatcher);
        }
    }

    public void callServerStart(MinecraftServer server) {
        if (this.calledServerStart) {
            throw new IllegalStateException("Server start already called");
        }
        this.calledServerStart = true;
        for (PluginBase plugin : this.loadedPlugins.keySet()) {
            plugin.onServerStart(server);
        }
    }

    public <P extends PluginBase> P getPlugin(String name) {
        for (PluginBase plugin : this.loadedPlugins.keySet()) {
            if (plugin.getMeta().getName().equalsIgnoreCase(name)) {
                return (P) plugin;
            }
        }
        return null;
    }

    public Collection<PluginBase> getPlugins() {
        return this.loadedPlugins.keySet();
    }
}
