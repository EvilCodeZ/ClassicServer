package de.evilcodez.classicprotocol;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PacketOutputStream extends DataOutputStream {

    private final TcpConnection connection;

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public PacketOutputStream(TcpConnection connection, OutputStream out) {
        super(out);
        this.connection = connection;
    }

    public TcpConnection getConnection() {
        return connection;
    }

    public void writeString(String s) throws IOException {
        final byte[] stringBytes = new byte[64];
        Arrays.fill(stringBytes, (byte) 32);
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length && i < stringBytes.length; i++) {
            stringBytes[i] = bytes[i];
        }
        this.write(stringBytes);
    }

    public void writeByteArray(byte[] data) throws IOException {
        final byte[] stringBytes = new byte[1024];
        for (int i = 0; i < data.length && i < stringBytes.length; i++) {
            stringBytes[i] = data[i];
        }
        this.write(stringBytes);
    }
}
