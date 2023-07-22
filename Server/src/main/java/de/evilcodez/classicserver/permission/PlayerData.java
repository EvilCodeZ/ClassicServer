package de.evilcodez.classicserver.permission;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class PlayerData {

    private String passwordHash;
    private String permissionGroup;
    private String lastIpAddress;

    public PlayerData(String passwordHash, String permissionGroup, String lastIpAddress) {
        this.passwordHash = passwordHash;
        this.permissionGroup = permissionGroup;
        this.lastIpAddress = lastIpAddress;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLastIpAddress() {
        return lastIpAddress;
    }

    public void setLastIpAddress(String lastIpAddress) {
        this.lastIpAddress = lastIpAddress;
    }

    public static String hashPassword(String password) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return Objects.equals(passwordHash, that.passwordHash) && Objects.equals(permissionGroup, that.permissionGroup) && Objects.equals(lastIpAddress, that.lastIpAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passwordHash, permissionGroup, lastIpAddress);
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "passwordHash='" + passwordHash + '\'' +
                ", permissionGroup=" + permissionGroup +
                ", lastIpAddress='" + lastIpAddress + '\'' +
                '}';
    }
}
