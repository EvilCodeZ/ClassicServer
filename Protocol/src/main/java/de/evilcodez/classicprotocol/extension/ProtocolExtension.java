package de.evilcodez.classicprotocol.extension;

import de.evilcodez.classicprotocol.packet.impl.ext.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ProtocolExtension {

    CLICK_DISTANCE("ClickDistance", null),
    CUSTOM_BLOCKS("CustomBlocks", null),
    HELD_BLOCK("HeldBlock", null),
    TEXT_HOT_KEY("TextHotKey", null),
    EXT_PLAYER_LIST("ExtPlayerList", null),
    ENV_COLORS("EnvColors", null),
    SELECTION_CUBOID("SelectionCuboid", null),
    BLOCK_PERMISSIONS("BlockPermissions", r -> r.registerPacket(28, Packet28SetBlockPermissions.class), 1),
    CHANGE_MODEL("ChangeModel", null),
    ENV_MAP_APPEARANCE("EnvMapAppearance", null),
    ENV_WEATHER_TYPE("EnvWeatherType", null),
    HACK_CONTROL("HackControl", r -> r.registerPacket(32, Packet32HackControl.class), 1),
    EMOTE_FIX("EmoteFix", null),
    MESSAGE_TYPES("MessageTypes", null),
    LONGER_MESSAGES("LongerMessages", r -> {}, 1),
    FULL_CP437("FullCP437", null),
    BLOCK_DEFINITIONS("BlockDefinitions", null),
    BLOCK_DEFINITIONS_EXT("BlockDefinitionsExt", null),
    TEXT_COLORS("TextColors", null),
    BULK_BLOCK_UPDATE("BulkBlockUpdate", r -> r.registerPacket(38, Packet38BulkBlockUpdate.class), 1),
    ENV_MAP_ASPECT("EnvMapAspect", null),
    PLAYER_CLICK("PlayerClick", null),
    ENTITY_PROPERTY("EntityProperty", null),
    EXT_ENTITY_POSITIONS("ExtEntityPositions", r -> {}, 1),
    TWO_WAY_PING("TwoWayPing", r -> r.registerPacket(43, Packet43TwoWayPing.class), 1),
    INVENTORY_ORDER("InventoryOrder", null),
    INSTANT_MOTD("InstantMOTD", null),
    FAST_MAP("FastMap", null),
    EXTENDED_TEXTURES("ExtendedTextures", null),
    SET_HOTBAR("SetHotbar", null),
    SET_SPAWNPOINT("SetSpawnpoint", r -> r.registerPacket(46, Packet46SetSpawnPoint.class), 1),
    VELOCITY_CONTROL("VelocityControl", null),
    CUSTOM_PARTICLES("CustomParticles", null),
    CUSTOM_MODELS("CustomModels", null);

    private final String name;
    private final ExtensionRegistry extensionRegistry;
    private final Set<Integer> supportedVersions;

    ProtocolExtension(String name, ExtensionRegistry extensionRegistry, int... supportedVersions) {
        this.name = name;
        this.extensionRegistry = extensionRegistry;
        this.supportedVersions = new HashSet<>();
        for (int supportedVersion : supportedVersions) {
            this.supportedVersions.add(supportedVersion);
        }
    }

    public static ProtocolExtension byName(String name) {
        return Arrays.stream(values()).filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

    public static ProtocolExtension byNameAndVersion(String name, int version) {
        final ProtocolExtension extension = Arrays.stream(values()).filter(e -> e.name.equals(name)).findFirst().orElse(null);
        if(extension == null || !extension.supportsVersion(version)) {
            return null;
        }
        return extension;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

    public boolean supportsVersion(int version) {
        return this.supportedVersions.contains(version);
    }

    public Set<Integer> getSupportedVersions() {
        return supportedVersions;
    }

    public int getHighestSupportedVersion() {
        int highest = 0;
        for (Integer supportedVersion : this.supportedVersions) {
            if(supportedVersion > highest) highest = supportedVersion;
        }
        return highest;
    }

    public boolean isSupported() {
        return !this.supportedVersions.isEmpty();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
