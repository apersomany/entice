package dev.aperso.entice.packet;

import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;

import java.nio.ByteBuffer;

public class PacketSystem {
    private static final ThreadSafeFury fury = Fury.builder().buildThreadSafeFury();

    public static void register(Class<?> packetClass) {
        fury.register(packetClass);
    }

    public static byte[] encode(Object object) {
        return fury.serialize(object);
    }

    public static Object decode(byte[] buffer) {
        return fury.deserialize(buffer);
    }

    public static Object decode(ByteBuffer buffer) {
        return fury.deserialize(buffer);
    }
}
