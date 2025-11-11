package com.zarnab.panel.core.config;

import com.zarnab.panel.clients.dto.FlatPersonInquiryResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, FlatPersonInquiryResponse> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, FlatPersonInquiryResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use Jackson2JsonRedisSerializer for values
        Jackson2JsonRedisSerializer<FlatPersonInquiryResponse> jsonSerializer = new Jackson2JsonRedisSerializer<>(FlatPersonInquiryResponse.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
