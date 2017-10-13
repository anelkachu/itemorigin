package itemorigin.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.hazelcast.core.Hazelcast;

import itemorigin.client.AustriaClient;
import itemorigin.client.GepirClient;
import itemorigin.client.NewAecocClient;
import itemorigin.config.CacheConfig;

@Component
public class CacheService {

	private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

	private AtomicInteger abroadCounter = new AtomicInteger(0);

	public AtomicInteger getAbroadCounter() {
		return abroadCounter;
	}

	@Autowired
	CountryService countryService;

	@Autowired
	PostalCodeService postalCodeService;

	private Map<String, Map<String, String>> gtinCache = Hazelcast.getHazelcastInstanceByName(CacheConfig.INSTANCE_NAME)
			.getMap(CacheConfig.GTIN_CACHE);

	public Map<String, String> getItemInfo(String gtin) {
		String paddedGtin13 = Strings.padStart(gtin, 13, '0');
		String paddedGtin14 = Strings.padStart(gtin, 14, '0');

		Map<String, String> cached = gtinCache.get(paddedGtin14);
		if (cached != null) {
			LOG.info("Found {} in cache", paddedGtin14);
			return gtinCache.get(paddedGtin14);
		} else {
			Map<String, String> mapReturn = new HashMap<String, String>();
			String countryCode = countryService.getCountryCodeByGlnId(paddedGtin13);
			mapReturn.put("countryCode", countryCode);
			try {
				if (!Strings.isNullOrEmpty(countryCode) && countryCode.equalsIgnoreCase("ES")) {
					// Aecoc client
					mapReturn = NewAecocClient.getAecocInfo(paddedGtin14);
				} else {
					// Gepir limited client
					if (abroadCounter.intValue() < 30) {
						// Gepir client
						mapReturn = GepirClient.getGepirInfo(paddedGtin14);
					} else {
						// Austrian client
						mapReturn = AustriaClient.getGepirAustriaInfo(paddedGtin14);
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
			// 697 -> Chinese bulb: administered country.
			if (Strings.isNullOrEmpty(countryCode)) {
				countryCode = mapReturn.get("countryCode");
			}

			// Enriching the info
			String postalCode = mapReturn.get("postalCode");
			if (!Strings.isNullOrEmpty(postalCode)) {
				if (!Strings.isNullOrEmpty(postalCode) && countryCode.equalsIgnoreCase("ES")) {
					String caCode = postalCodeService.getCaCode(postalCode);
					String caName = postalCodeService.getCaName(caCode);
					mapReturn.put("caCode", caCode);
					mapReturn.put("caName", caName);
				} else {
					// BE/LUX case
					if (countryCode.equalsIgnoreCase("BE") && postalCode.contains("L")) {
						countryCode = "LU";
						mapReturn.put("countryCode", countryCode);
					}
				}
			}
			String countryName = countryService.getCountryNameByCode(countryCode);
			mapReturn.put("countryName", countryName);
			// Storing in cache
			gtinCache.put(paddedGtin14, mapReturn);
			return mapReturn;
		}
	}
}
