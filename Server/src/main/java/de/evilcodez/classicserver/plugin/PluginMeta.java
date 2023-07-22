package de.evilcodez.classicserver.plugin;

public final class PluginMeta {

    private final String name;
    private final String mainClass;
    private final String version;
    private final String[] authors;
    private final String description;
    private final boolean canTransform;

    PluginMeta(String name, String mainClass, String version, String[] authors, String description, boolean canTransform) {
        this.name = name;
        this.mainClass = mainClass;
        this.version = version;
        this.authors = authors;
        this.description = description;
        this.canTransform = canTransform;
    }

    public String getName() {
        return name;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransform() {
        return canTransform;
    }
}
