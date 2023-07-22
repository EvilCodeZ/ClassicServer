package de.evilcodez.classicserver.option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import de.evilcodez.classicserver.player.ServerNetworkHandler;
import de.evilcodez.classicserver.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServerMessages {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    @SerializedName("join_messages")
    public List<String> joinMessages = new ArrayList<>(Collections.singletonList("&7Welcome &6%player_name%&7!"));

    @SerializedName("worlds")
    public Map<String, List<String>> worldMessages = Collections.singletonMap("main", Collections.singletonList("&7Use &8/&7goto &8<&aWorld&8> &7to switch the world!"));

    private String insertPlaceholders(ServerNetworkHandler player, String message) {
        return message.replace("%player_name%", player.getPlayer().getName())
                .replace("%player_ip%", player.getConnection().getRemoteAddress().getAddress().getHostAddress());
    }

    public void sendJoinMessages(ServerNetworkHandler player) {
        for (String message : joinMessages) {
            player.getPlayer().sendChatMessage(this.insertPlaceholders(player, message));
        }
    }

    public void sendWorldMessages(ServerNetworkHandler player, World world) {
        for (String message : worldMessages.getOrDefault(world.getName(), Collections.emptyList())) {
            player.getPlayer().sendChatMessage(this.insertPlaceholders(player, message));
        }
    }

    public static ServerMessages loadMessages(File file) {
        if (!file.exists()) {
            final ServerMessages messages = new ServerMessages();
            messages.saveMessages(file);
            return messages;
        }
        try (final FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, ServerMessages.class);
        } catch (Exception ex) {
            System.out.println("[Warning] Failed to load messages: " + ex.getMessage());

            final ServerMessages messages = new ServerMessages();
            messages.saveMessages(file);
            return messages;
        }
    }

    public void saveMessages(File file) {
        try {
            final FileWriter fw = new FileWriter(file);
            fw.write(GSON.toJson(this));
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            System.out.println("[Warning] Failed to save messages: " + ex.getMessage());
        }
    }
}
