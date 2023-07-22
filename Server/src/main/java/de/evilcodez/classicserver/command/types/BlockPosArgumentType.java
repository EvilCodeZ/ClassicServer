package de.evilcodez.classicserver.command.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.evilcodez.classicserver.utils.BlockPos;

import java.util.Arrays;
import java.util.Collection;

public class BlockPosArgumentType implements ArgumentType<BlockPos> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "34 64 78", "-23 78 94");

    @Override
    public BlockPos parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        final int x = reader.readInt();
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            final int y = reader.readInt();
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                final int z = reader.readInt();
                return new BlockPos(x, y, z);
            } else {
                reader.setCursor(i);
                throw new SimpleCommandExceptionType(() -> "Incomplete coordinate.").createWithContext(reader);
            }
        } else {
            reader.setCursor(i);
            throw new SimpleCommandExceptionType(() -> "Incomplete coordinate.").createWithContext(reader);
        }
    }

    public static BlockPosArgumentType blockPos() {
        return new BlockPosArgumentType();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
