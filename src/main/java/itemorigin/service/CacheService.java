package itemorigin.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.hazelcast.core.Hazelcast;

import itemorigin.client.GepirClient;
import itemorigin.client.NewAecocClient;
import itemorigin.config.CacheConfig;

@Component
public class CacheService {

	@Autowired
	CountryService countryService;

	@Autowired
	PostalCodeService postalCodeService;

	private Map<String, Map<String, String>> gtinCache = Hazelcast.getHazelcastInstanceByName(CacheConfig.INSTANCE_NAME)
			.getMap(CacheConfig.GTIN_CACHE);

	public Map<String, String> getItemInfo(String gtin) {
		Stopwatch watch = Stopwatch.createStarted();

		Map<String, String> cached = gtinCache.get(gtin);
		System.out.println("Checkpoint #1 " + watch.elapsed(TimeUnit.MILLISECONDS));

		if (cached != null) {
			return gtinCache.get(gtin);
		} else {
			Map<String, String> mapReturn = new HashMap<String, String>();
			String countryCode = countryService.getCountryCodeByGlnId(gtin);
			System.out.println("Checkpoint #2 " + watch.elapsed(TimeUnit.MILLISECONDS));
			if (!Strings.isNullOrEmpty(countryCode) && countryCode.equalsIgnoreCase("ES")) {
				// Aecoc client
				try {
					mapReturn = NewAecocClient.getAecocInfo(gtin);
					System.out.println("Checkpoint #3 " + watch.elapsed(TimeUnit.MILLISECONDS));
				} catch (IOException e) {
					e.printStackTrace();
				}
				String caCode = postalCodeService.getCaCode(mapReturn.get("postalCode"));
				System.out.println("Checkpoint #4 " + watch.elapsed(TimeUnit.MILLISECONDS));
				String caName = postalCodeService.getCaName(caCode);
				System.out.println("Checkpoint #5 " + watch.elapsed(TimeUnit.MILLISECONDS));
				mapReturn.put("caCode", caCode);
				mapReturn.put("caName", caName);
				String countryName = countryService.getCountryNameByCode(countryCode);
				System.out.println("Checkpoint #6 " + watch.elapsed(TimeUnit.MILLISECONDS));
				mapReturn.put("countryName", countryName);
			} else {
				// Gepir limited client
				String ret = "";
				try {
					ret = GepirClient.prepareConnection().data("keyValue", gtin).execute().body();
					System.out.println("Checkpoint #8 " + watch.elapsed(TimeUnit.MILLISECONDS));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(ret);
				System.out.println("Checkpoint #9 " + watch.elapsed(TimeUnit.MILLISECONDS));

				String partyName = (String) flattenedJsonMap.get("gepirParty.partyDataLine.gS1KeyLicensee.partyName");
				String street = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.streetAddressOne");
				String lastChange = (String) flattenedJsonMap.get("gepirParty.partyDataLine.lastChangeDate");
				String city = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.city");
				String postalCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.postalCode");
				// Country
				countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.countryCode._");
				if (Strings.isNullOrEmpty(countryCode)) {
					countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.countryAdministered");
				}
				String countryName = countryService.getCountryNameByCode(countryCode);
				System.out.println("Checkpoint #10 " + watch.elapsed(TimeUnit.MILLISECONDS));
				
				// Region based on postalCode
				if (countryCode.equalsIgnoreCase("ES") && !Strings.isNullOrEmpty(postalCode)) {
					String caCode = postalCodeService.getCaCode(postalCode);
					String caName = postalCodeService.getCaName(caCode);
					mapReturn.put("caCode", caCode);
					mapReturn.put("caName", caName);
				}

				mapReturn.put("partyName", partyName);
				mapReturn.put("street", street);
				mapReturn.put("lastChange", lastChange);
				mapReturn.put("countryCode", countryCode);
				mapReturn.put("countryName", countryName);
				mapReturn.put("postalCode", postalCode);
				mapReturn.put("city", city);
			}
			gtinCache.put(gtin, mapReturn);
			System.out.println("Checkpoint #7 " + watch.elapsed(TimeUnit.MILLISECONDS));
			return mapReturn;
		}
	}
}
