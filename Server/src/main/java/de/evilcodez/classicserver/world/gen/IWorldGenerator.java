package de.evilcodez.classicserver.world.gen;

import de.evilcodez.classicserver.world.Level;

public interface IWorldGenerator {

    FlatWorldGenerator FLAT_WORLD_GENERATOR = new FlatWorldGenerator();
    NormalWorldGenerator NORMAL_WORLD_GENERATOR = new NormalWorldGenerator();

    void generate(Level level);

    void populate(Level level);
}
