package com.wei.rpc.serializer;

import java.io.IOException;

public class JsonSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserializer(byte[] bytes, Class<T> type) throws IOException {
        return null;
    }
}
