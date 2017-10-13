package itemorigin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;

@Configuration
public class CacheConfig {

	public static String INSTANCE_NAME = "hazelcast-instance";
	public static String GTIN_CACHE = "GTIN_CACHE";

	@Bean
	public Config hazelCastConfig() {
		// Static content non evictable
		MapConfig countryMap = new MapConfig().setName("glnCountryCode");

		MapConfig gtinMap = new MapConfig().setName(GTIN_CACHE)
				.setMaxSizeConfig(new MaxSizeConfig(500, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
				.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(60 * 60 * 24);

		Config cfg = new Config().setInstanceName(INSTANCE_NAME).addMapConfig(countryMap).addMapConfig(gtinMap);
		cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		return cfg;
	}
}
