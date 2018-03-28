package mbd.s3ackup.daemon.z_boot.config;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Application caching
 * 
 * @author mark
 *
 */
@Configuration
@EnableCaching
public class ConfigAppCaching extends CachingConfigurerSupport {

	private static final Logger logger = LoggerFactory.getLogger(ConfigAppCaching.class);

	public static final String DEFAULT_CACHE = "default";
	private static final Cache<Object, Object> defaultCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).recordStats().build();

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager(DEFAULT_CACHE) {
			@Override
			protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(String name) {
				return defaultCache;
			}
		};
		return cacheManager;
	}

	@Override
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName());
				sb.append(method.getName());
				for (Object param : params) {
					sb.append(param.toString());
				}
				return sb.toString();
			}
		};
	}

	@Scheduled(fixedDelay = 10 * 1000)
	public void printCandleCacheStats() {
		logger.info("default cache size[{}] stats[{}]", defaultCache.estimatedSize(), defaultCache.stats());
	}
}