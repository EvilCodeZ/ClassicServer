package de.evilcodez.classicserver.permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionGroup {

    private final String name;
    private final List<String> permissions;
    private PermissionGroup parent;
    private boolean showPrefix;
    private String prefixColorCode;

    public PermissionGroup(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getAllPermissions() {
        final Set<String> list = new HashSet<>();
        PermissionGroup group = this;
        while(group != null) {
            list.addAll(group.getPermissions());
            group = group.getParent();
        }
        return new ArrayList<>(list);
    }

    public Set<String> getAllPermissionsSet() {
        final Set<String> permissions = new HashSet<>();
        PermissionGroup group = this;
        while(group != null) {
            permissions.addAll(group.getPermissions());
            group = group.getParent();
        }
        return permissions;
    }

    public boolean hasPermission(String permission) {
        if(permission.equals("group." + name.toLowerCase())) {
            return true;
        }
        final Set<String> perms = this.getAllPermissionsSet();
        return perms.contains("*") || perms.contains(permission);
    }

    public boolean isPrefixVisible() {
        return this.showPrefix;
    }

    public void setPrefixVisible(boolean showPrefix) {
        this.showPrefix = showPrefix;
    }

    public String getPrefixColorCode() {
        return prefixColorCode;
    }

    public void setPrefixColorCode(String prefixColorCode) {
        this.prefixColorCode = prefixColorCode;
    }

    public PermissionGroup getParent() {
        return parent;
    }

    public void setParent(PermissionGroup parent) {
        this.parent = parent;
    }
}
