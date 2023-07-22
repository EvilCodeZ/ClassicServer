package de.evilcodez.classicserver.option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import de.evilcodez.classicserver.utils.BlockPos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ServerOptions {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    @SerializedName("server_port")
    public int serverPort = 25565;

    @SerializedName("max_players")
    public int maxPlayers = -1; // -1 = unlimited

    @SerializedName("max_players_per_ip")
    public int maxPlayersPerIp = 3; // -1 = unlimited

    @SerializedName("spam_protection")
    public boolean spamProtection = true; // If false, then everyone will be able to spam the chat as much as they want

    @SerializedName("block_spam_protection")
    public boolean blockSpamProtection = true; // If false, then GameRule.BLOCK_CHANGE_THRESHOLD will be disabled on all worlds

    @SerializedName("default_world_name")
    public String defaultWorldName = "main";

    @SerializedName("default_world_size")
    public BlockPos defaultWorldSize = new BlockPos(256, 128, 256);

    @SerializedName("default_world_generator")
    public String defaultWorldGenerator = "flat";

    @SerializedName("offline_mode_auth")
    public boolean offlineModeAuth = false; // AuthMe like auth system: /register <pass> <pass>, /login <pass>

    @SerializedName("server_name")
    public String serverName = "Classic Server";

    @SerializedName("server_ip")
    public String serverIp = "0.0.0.0";

    @SerializedName("server_brand")
    public String serverBrand = "Classic Server";

    @SerializedName("server_motds")
    public final List<String> serverMotds = new ArrayList<>(Arrays.asList("A Minecraft Classic Server", "c0.30 Classic Server by EvilCodeZ"));

    @SerializedName("chunk_packet_limit_per_tick")
    public int chunkPacketLimitPerTick = 5; // Global limit that defines how many LevelData packets can be send per tick

    @SerializedName("betacraft")
    public BetacraftServerListOptions betacraft = new BetacraftServerListOptions();

    public static ServerOptions loadOptions(File file) {
        if (!file.exists()) {
            final ServerOptions options = new ServerOptions();
            options.saveOptions(file);
            return options;
        }
        try (final FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, ServerOptions.class);
        } catch (Exception ex) {
            System.out.println("[Warning] Failed to load options: " + ex.getMessage());

            final ServerOptions options = new ServerOptions();
            options.saveOptions(file);
            return options;
        }
    }

    public void saveOptions(File file) {
        try {
            final FileWriter fw = new FileWriter(file);
            fw.write(GSON.toJson(this));
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            System.out.println("[Warning] Failed to save options: " + ex.getMessage());
        }
    }

    public static class BetacraftServerListOptions {

        @SerializedName("enabled")
        public boolean enabled = false;

        @SerializedName("salt")
        public String salt = String.valueOf(ThreadLocalRandom.current().nextLong());
    }
}
