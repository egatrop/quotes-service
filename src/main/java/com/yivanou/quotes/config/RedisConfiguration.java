package com.yivanou.quotes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yivanou.quotes.repository.entity.CandleStick;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    @Autowired
    private final RedisProperties properties;

    @Autowired
    private final ObjectMapper mapper;

    @Bean
    public RedisCommands<String, CandleStick> getConnection() {
        RedisClient client = RedisClient.create(RedisURI.create(properties.getHost(), properties.getPort()));

        return client.connect(
                CompressionCodec.valueCompressor(new DataRedisCodec(mapper), CompressionCodec.CompressionType.GZIP)
        ).sync();
    }

    private static class DataRedisCodec implements RedisCodec<String, CandleStick> {

        private final ObjectMapper mapper;
        private final StringCodec utf8 = new StringCodec(StandardCharsets.UTF_8);

        public DataRedisCodec(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public String decodeKey(ByteBuffer byteBuffer) {
            return utf8.decodeKey(byteBuffer);
        }

        @SneakyThrows
        @Override
        public CandleStick decodeValue(ByteBuffer byteBuffer) {
            return mapper.readValue(byteBuffer.array(), CandleStick.class);
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return utf8.encodeKey(key);
        }

        @SneakyThrows
        @Override
        public ByteBuffer encodeValue(CandleStick value) {
            return ByteBuffer.wrap(mapper.writeValueAsBytes(value));
        }
    }
}
