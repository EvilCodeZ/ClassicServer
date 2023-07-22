package de.evilcodez.classicserver.world;

public enum GameRule {

    TNT_EXPLOSION(true), // Tnt explosions enabled
    WATER_PHYSICS(true), // If water should spread
    LAVA_PHYSICS(true), // If lava should spread
    BLOCK_TICKING(true), // If blocks should generally be ticked (if false, the first 3 rules will be also disabled)
    WORLD_MODIFICATION(""), // If not empty the player needs a permission like modify.this.world to modify blocks in this world
    ENTER_WORLD(""), // If not empty the player needs a permission like enter.this.world to join this world
    SPAWN_PROTECTION(0), // Spawn protection radius (Bypass perm: world.modifyspawn & world.modifyspawn.<worldname>)
    CHEATS_ENABLED(true), // If cheats are DISABLED then movement checks will be performed (can legit flag players) (Bypass perm: world.cheats & world.cheats.<worldname>)
    BLOCK_RANGE(7), // If player modifies block outside this range then he will be kicked for "&cRange cheat!"
    BLOCK_CHANGE_THRESHOLD(25); // If player places/breaks multiple blocks in this time (milliseconds) then the blocks will be set back

    private final Object defaultValue;

    GameRule(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
