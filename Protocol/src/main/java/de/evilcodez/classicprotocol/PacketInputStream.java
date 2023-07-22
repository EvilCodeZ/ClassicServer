package de.evilcodez.classicprotocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PacketInputStream extends DataInputStream {

    private final TcpConnection connection;

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public PacketInputStream(TcpConnection connection, InputStream in) {
        super(in);
        this.connection = connection;
    }

    public TcpConnection getConnection() {
        return connection;
    }

    public String readString() throws IOException {
        final byte[] stringBytes = new byte[64];
        this.readFully(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8).trim();
    }

    public byte[] readByteArray() throws IOException {
        final byte[] data = new byte[1024];
        this.readFully(data);
        return data;
    }
}
