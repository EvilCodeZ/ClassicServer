package de.evilcodez.classicserver.permission;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupManager {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private final File permissionsFile;
    private final Map<String, PermissionGroup> permissionGroupMap;
    private PermissionGroup defaultGroup;
    private PermissionGroup administratorGroup;

    public GroupManager() {
        this.permissionsFile = new File("permissions.json");
        this.permissionGroupMap = new HashMap<>();
        this.load();
    }

    public Map<String, PermissionGroup> getPermissionGroups() {
        return permissionGroupMap;
    }

    public void addGroup(PermissionGroup group) {
        permissionGroupMap.put(group.getName(), group);
    }

    public PermissionGroup getGroup(String name) {
        return permissionGroupMap.get(name);
    }

    public PermissionGroup getGroupOrDefault(String name) {
        return permissionGroupMap.getOrDefault(name, defaultGroup);
    }

    public void removeGroup(String name) {
        permissionGroupMap.remove(name);
    }

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    public PermissionGroup getAdministratorGroup() {
        return administratorGroup;
    }

    public void load() {
        permissionGroupMap.clear();
        if(!this.permissionsFile.exists()) {
            defaultGroup = new PermissionGroup("Player");
            defaultGroup.getPermissions().add("server.goto");
            defaultGroup.getPermissions().add("server.ping");
            defaultGroup.getPermissions().add("server.ping.other");
            permissionGroupMap.put(defaultGroup.getName(), defaultGroup);

            administratorGroup = new PermissionGroup("Admin");
            administratorGroup.setParent(defaultGroup);
            administratorGroup.getPermissions().add("*");
            permissionGroupMap.put(administratorGroup.getName(), administratorGroup);
            this.save();
        }
        try (final FileReader reader = new FileReader(permissionsFile)) {
            final JsonObject root = GSON.fromJson(reader, JsonObject.class);
            final JsonArray groups = root.getAsJsonArray("groups");
            final String defaultGroupName = root.get("default").getAsString();
            final String administratorGroupName = root.get("administrator").getAsString();

            for (JsonElement element : groups) {
                final JsonObject entry = element.getAsJsonObject();
                final JsonArray permissions = entry.getAsJsonArray("permissions");
                final String name = entry.get("name").getAsString();
                final List<String> list = new ArrayList<>();

                for (JsonElement permissionEntry : permissions) {
                    list.add(permissionEntry.getAsString());
                }

                boolean showPrefix = false;
                String prefixColorCode = "";
                final JsonObject prefix = entry.getAsJsonObject("prefix");
                if(prefix != null) {
                    showPrefix = prefix.get("show").getAsBoolean();
                    prefixColorCode = prefix.has("color_code") ? prefix.get("color_code").getAsString() : "";
                }

                final PermissionGroup group = new PermissionGroup(name);
                group.setPrefixVisible(showPrefix);
                group.setPrefixColorCode(prefixColorCode);
                group.getPermissions().addAll(list);
                permissionGroupMap.put(group.getName(), group);
            }

            for (JsonElement element : groups) {
                final JsonObject entry = element.getAsJsonObject();
                final String name = entry.get("name").getAsString();
                final String parentName = entry.has("parent") ? entry.get("parent").getAsString() : null;
                final PermissionGroup group = permissionGroupMap.get(name);
                final PermissionGroup parent = parentName == null ? null : permissionGroupMap.get(parentName);
                if(parent == null && parentName != null) {
                    throw new RuntimeException("Parent '" + parentName + "' does not exist for permission group '" + name + "'");
                }
                group.setParent(parent);
            }

            defaultGroup = permissionGroupMap.get(defaultGroupName);
            if(defaultGroup == null) {
                throw new RuntimeException("Default group does not exist: " + defaultGroupName);
            }
            administratorGroup = permissionGroupMap.get(administratorGroupName);
            if(administratorGroup == null) {
                throw new RuntimeException("Administrator group does not exist: " + administratorGroupName);
            }
        }catch (Exception e) {
            throw new RuntimeException("Failed to load permissions!", e);
        }
    }

    public void save() {
        final JsonObject root = new JsonObject();
        final JsonArray groups = new JsonArray();

        for (PermissionGroup group : permissionGroupMap.values()) {
            final JsonObject entry = new JsonObject();
            entry.addProperty("name", group.getName());
            if(group.getParent() != null) {
                entry.addProperty("parent", group.getParent().getName());
            }
            final JsonArray permissions = new JsonArray();
            group.getPermissions().forEach(permissions::add);
            entry.add("permissions", permissions);

            final JsonObject prefix = new JsonObject();
            prefix.addProperty("show", group.isPrefixVisible());
            prefix.addProperty("color_code", group.getPrefixColorCode());
            entry.add("prefix", prefix);

            groups.add(entry);
        }

        root.add("groups", groups);
        root.addProperty("default", defaultGroup.getName());
        root.addProperty("administrator", administratorGroup.getName());

        try {
            final FileWriter fw = new FileWriter(permissionsFile);
            fw.write(GSON.toJson(root));
            fw.flush();
            fw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
