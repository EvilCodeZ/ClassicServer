package de.evilcodez.classicserver.utils.serverlist;

import de.evilcodez.classicprotocol.packet.PacketRegistry;
import de.evilcodez.classicserver.MinecraftServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BetacraftPing extends Thread {

    private static final URL BETACRAFT_URL;
    private final MinecraftServer server;
    private String editUrl;

    public BetacraftPing(MinecraftServer server) {
        this.server = server;
        this.setName("Betacraft Ping Thread");
        this.setDaemon(true);
    }

    private void sendHeartbeat() throws IOException {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("name", server.getServerOptions().serverName);
        payload.put("users", server.getPlayersOnline());
        payload.put("max", server.getMaxPlayers());
        payload.put("public", "True");
        payload.put("port", ((InetSocketAddress) server.getServerConnection().getServerChannel().localAddress()).getPort());
        payload.put("salt", server.getServerOptions().betacraft.salt);
        payload.put("admin-slot", false);
        payload.put("version", PacketRegistry.PROTOCOL_VERSION);
        final StringBuilder payloadBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if(payloadBuilder.length() > 0) payloadBuilder.append("&");
            payloadBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        final byte[] payloadBytes = payloadBuilder.toString().getBytes(StandardCharsets.UTF_8);

        final HttpURLConnection connection = (HttpURLConnection) BETACRAFT_URL.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(payloadBytes.length));
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.getOutputStream().write(payloadBytes);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        connection.disconnect();

        String responseUrl = response.toString();
        if(responseUrl.contains("\"")) {
            responseUrl = responseUrl.substring(responseUrl.indexOf("\"") + 1, responseUrl.lastIndexOf("\""));
        }
        this.editUrl = responseUrl;
    }

    @Override
    public void run() {
        int failsInRow = 0;
        while (server.isRunning()) {
            try {
                this.sendHeartbeat();
                failsInRow = 0;
            }catch (Exception e) {
                System.out.println("[BetaCraftPing] Failed to ping server list! Error: " + e.getMessage());
                ++failsInRow;
            }
            if(failsInRow > 5) {
                System.out.println("[BetaCraftPing] More than 5 server list pings failed in a row, canceling ping thread.");
                return;
            }
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public String getEditUrl() {
        return editUrl;
    }

    static {
        try {
            BETACRAFT_URL = new URL("https://betacraft.uk/heartbeat.jsp");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
