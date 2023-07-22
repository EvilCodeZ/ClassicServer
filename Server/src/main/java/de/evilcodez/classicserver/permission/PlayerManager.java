package de.evilcodez.classicserver.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PlayerManager {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private final File playersDir;

    public PlayerManager() {
        this.playersDir = new File("players");
        if(!playersDir.exists()) {
            playersDir.mkdir();
        }
    }

    public PlayerData getPlayerData(String username) {
        final File file = new File(playersDir, username.toLowerCase() + ".json");
        if(!file.exists()) {
            return null;
        }
        try (final FileReader reader = new FileReader(file)) {
            final JsonObject root = GSON.fromJson(reader, JsonObject.class);
            String pw = null;
            if(root.has("password")) {
                pw = root.get("password").getAsString();
            }
            final String permissionGroup = root.get("permission_group").getAsString();
            final String lastIP = root.get("last_ip").getAsString();
            return new PlayerData(pw, permissionGroup, lastIP);
        } catch (Exception ex) {
            return null;
        }
    }

    public void savePlayerData(String username, PlayerData playerData) {
        final File file = new File(playersDir, username.toLowerCase() + ".json");
        final JsonObject root = new JsonObject();
        if(playerData.getPasswordHash() != null) {
            root.addProperty("password", playerData.getPasswordHash());
        }
        root.addProperty("permission_group", playerData.getPermissionGroup());
        root.addProperty("last_ip", playerData.getLastIpAddress());
        try {
            final FileWriter fw = new FileWriter(file);
            fw.write(GSON.toJson(root));
            fw.flush();
            fw.close();
        } catch (Exception ignored) {
        }
    }
}
