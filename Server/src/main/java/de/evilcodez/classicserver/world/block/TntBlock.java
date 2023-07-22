package de.evilcodez.classicserver.world.block;

import de.evilcodez.classicserver.utils.BlockPos;
import de.evilcodez.classicserver.world.GameRule;
import de.evilcodez.classicserver.world.World;
import me.dags.noise.Noise;
import me.dags.noise.Source;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TntBlock extends Block {

    public TntBlock(String name, int id) {
        super(name, id);
        this.setTickable(true);
    }

    @Override
    public void tick(World world, BlockPos pos) {
        if(!world.<Boolean>getGameRule(GameRule.TNT_EXPLOSION)) return;

        final Set<BlockPos> neighbors = new HashSet<>();
        neighbors.add(pos.add(1, 0, 0));
        neighbors.add(pos.add(0, 0, 1));
        neighbors.add(pos.add(-1, 0, 0));
        neighbors.add(pos.add(0, 0, -1));
        neighbors.add(pos.add(0, 1, 0));
        neighbors.add(pos.add(0, -1, 0));

        for (BlockPos neighbor : neighbors) {
            final Block block = world.getLevel().getBlock(neighbor);
            if(block == Blocks.RED_WOOL || block == Blocks.LAVA || block == Blocks.STATIONARY_LAVA) {
                this.explode(world, pos);
                break;
            }
        }

        super.tick(world, pos);
    }

    private void explode(World world, BlockPos pos) {
        try {
            final Random rnd = new Random();
            final Noise perlin = Source.perlin(4, 4);
            int range = 4 + rnd.nextInt(2);

            final BlockPos from = pos.subtract(range, range, range);
            final BlockPos to = pos.add(range, range, range);
            range -= 2;
            for (int y = from.getY(); y <= to.getY(); ++y) {
                for (int x = from.getX(); x <= to.getX(); ++x) {
                    for (int z = from.getZ(); z <= to.getZ(); ++z) {
                        double dx = x - pos.getX();
                        double dy = y - pos.getY();
                        double dz = z - pos.getZ();
                        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if(world.getLevel().getBlock(x, y, z) == Blocks.BEDROCK.getId()) {
                            continue;
                        }
                        if (d < range + 1 && perlin.getValue(x, y) * perlin.getValue(z, y) > 0.12F) {
                            world.updateBlock(x, y, z, 0);
                        }
                        final int currentBlock = world.getLevel().getBlock(x, y, z);
                        if (currentBlock == Blocks.TNT.getId()) {
                            this.explode(world, new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }catch (StackOverflowError ignored) {}
    }
}
