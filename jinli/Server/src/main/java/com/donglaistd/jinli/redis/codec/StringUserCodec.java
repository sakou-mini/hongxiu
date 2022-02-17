package com.donglaistd.jinli.redis.codec;

import com.donglaistd.jinli.database.entity.User;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.nio.ByteBuffer;

public class StringUserCodec implements RedisCodec<String, User> {

    private final JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        return (String) jdkSerializationRedisSerializer.deserialize(byteBuffer.array());
    }

    @Override
    public User decodeValue(ByteBuffer byteBuffer) {
        return (User) jdkSerializationRedisSerializer.deserialize(byteBuffer.array());
    }

    @Override
    public ByteBuffer encodeKey(String s) {
        byte[] bytes = jdkSerializationRedisSerializer.serialize(s);
        assert bytes != null;
        var byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }

    @Override
    public ByteBuffer encodeValue(User user) {
        byte[] bytes = jdkSerializationRedisSerializer.serialize(user);
        assert bytes != null;
        var byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }
}
