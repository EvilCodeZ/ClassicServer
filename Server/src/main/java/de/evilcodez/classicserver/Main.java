package de.evilcodez.classicserver;

import de.evilcodez.classicserver.plugin.Plugins;
import de.evilcodez.jni4j.JavaVM;
import de.evilcodez.jni4j.jplis.JPLIS;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class Main {

    public static Instrumentation instrumentation;

    public static void main(String[] args) throws Exception {
        final File pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            pluginsDir.mkdir();
        }
        final Plugins plugins = new Plugins();
        final File[] files = pluginsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".jar")) {
                    plugins.addPlugin(file);
                }
            }
            plugins.loadPlugins(() -> {
                if (instrumentation == null) {
                    try {
                        instrumentation = createInstrumentation();
                        System.out.println("Using runtime instrumentation because no alternative was found");
                    } catch (Exception e) {
                        System.err.println("Failed to create runtime instrumentation!");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                return instrumentation;
            });
        }
        new MinecraftServer(plugins).startServer();
    }

    public static void premain(String args, Instrumentation instrumentation) {
        Main.instrumentation = instrumentation;
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        Main.instrumentation = instrumentation;
    }

    private static Instrumentation createInstrumentation() throws Exception {
        final JavaVM vm = JavaVM.getRunningJavaVM();
        JPLIS.initialize();
        if (!JPLIS.isSupported()) {
            System.err.println("Runtime Instrumentation creation is not supported for this JVM build!");
            System.err.println("Please use the -javaagent:<Server Jar> jvm argument to load the agent!");
            System.err.println("Example: java -javaagent:Server.jar -jar Server.jar");
            System.exit(1);
        }
        return new JPLIS(vm.getHandle(), true, true, false, false).getInstrumentation();
    }
}
