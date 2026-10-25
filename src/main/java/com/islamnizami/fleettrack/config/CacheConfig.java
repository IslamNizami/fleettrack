package com.islamnizami.fleettrack.config;

import com.islamnizami.fleettrack.model.dto.driver.DriverResponse;
import com.islamnizami.fleettrack.model.dto.summary.FleetSummaryResponse;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleResponse;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

/**
 * Redis cache with JSON values and an expiry per cache.
 *
 *   vehicles       -> VehicleResponse      10 minutes
 *   drivers        -> DriverResponse       10 minutes
 *   fleet-summary  -> FleetSummaryResponse  1 minute
 */
@Configuration
public class CacheConfig {

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withCacheConfiguration("vehicles",
                        typed(defaults, VehicleResponse.class, Duration.ofMinutes(10)))
                .withCacheConfiguration("drivers",
                        typed(defaults, DriverResponse.class, Duration.ofMinutes(10)))
                .withCacheConfiguration("fleet-summary",
                        typed(defaults, FleetSummaryResponse.class, Duration.ofMinutes(1)))
                // evictions/puts happen after the DB transaction commits
                .transactionAware()
                .build();
    }

    private <T> RedisCacheConfiguration typed(RedisCacheConfiguration base,
                                              Class<T> type, Duration ttl) {
        return base.entryTtl(ttl).serializeValuesWith(
                SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(type)));
    }
}
