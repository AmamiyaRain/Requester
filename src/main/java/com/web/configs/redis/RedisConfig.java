package com.web.configs.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

@Data
@ToString
@EqualsAndHashCode
@Component
public class RedisConfig {
	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.password}")
	private String psd;

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, 6379);
		config.setPassword(psd);
		JedisConnectionFactory factory = new JedisConnectionFactory(config);
		return new JedisConnectionFactory(config);
	}
}