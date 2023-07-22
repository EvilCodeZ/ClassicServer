package de.evilcodez.classicserver.utils;

import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.command.CommandManager;
import de.evilcodez.classicserver.command.ICommandSource;
import de.evilcodez.classicserver.player.PlayerEntity;

import java.util.Scanner;

public class ConsoleCommandSource implements ICommandSource {

    public ConsoleCommandSource() {
        new Thread(this::run, "Console Thread").start();
    }

    private void run() {
        final MinecraftServer server = MinecraftServer.getInstance();
        final Scanner sc = new Scanner(System.in);
        while(server.isRunning()) {
            String ln = null;
            try {
                ln = sc.nextLine();
                if(ln.equalsIgnoreCase("/betacraft") || ln.equalsIgnoreCase("betacraft")) {
                    if(server.getBetacraftPing() != null && server.getBetacraftPing().getEditUrl() != null) {
                        System.out.println("Betacraft: " + server.getBetacraftPing().getEditUrl());
                    } else {
                        System.out.println("Betacraft: Not connected");
                    }
                    continue;
                }
                CommandManager.execute(ln, this);
            }catch (Exception ex) {
                System.out.println("[Error] Failed to run command: " + ln);
                ex.printStackTrace();
            }
        }
        System.out.println("Console Thread terminated.");
        sc.close();
    }

    @Override
    public void sendChatMessage(String message) {
        System.out.println("[Console] " + StringUtils.removeColorCodes(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public MinecraftServer getServer() {
        return MinecraftServer.getInstance();
    }

    @Override
    public PlayerEntity asPlayer() {
        return null;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }
}
