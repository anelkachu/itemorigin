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
		MapConfig countryMap = new MapConfig().setName("glnCountryCode")
				.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
				.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(600);

		MapConfig gtinMap = new MapConfig().setName(GTIN_CACHE)
				.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
				.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(600);

		Config cfg = new Config().setInstanceName(INSTANCE_NAME).addMapConfig(countryMap).addMapConfig(gtinMap);
		cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		return cfg;
	}
}
