package de.evilcodez.classicserver.command.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.evilcodez.classicserver.world.block.Block;
import de.evilcodez.classicserver.world.block.Blocks;

import java.util.Arrays;
import java.util.Collection;

public class BlockArgumentType implements ArgumentType<Block> {

    private static final Collection<String> EXAMPLES = Arrays.asList("dirt", "stone", "2", "5");

    @Override
    public Block parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final String name = reader.readString();
        Block block = Arrays.stream(Blocks.blocks).filter(b -> b.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if(block != null) {
            return block;
        }
        reader.setCursor(start);
        try {
            int id = reader.readInt();
            if(id < 0 || id >= Blocks.blocks.length) {
                throw new SimpleCommandExceptionType(() -> "Invalid block id!").createWithContext(reader);
            }
            block = Blocks.blocks[id];
        }catch (Throwable t) {
            throw new SimpleCommandExceptionType(() -> "Block not found!").createWithContext(reader);
        }
        return block;
    }

    public static BlockArgumentType block() {
        return new BlockArgumentType();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
