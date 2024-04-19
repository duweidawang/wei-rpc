package com.wei.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    <T> byte[] serialize(T object) throws IOException;
    <T> T deserializer(byte[] bytes ,Class<T> type) throws IOException;
}
