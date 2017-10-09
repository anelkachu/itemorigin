package itemorigin.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.wnameless.json.flattener.JsonFlattener;
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
		Map<String, String> cached = gtinCache.get(gtin);
		if (cached != null) {
			return gtinCache.get(gtin);
		} else {
			Map<String, String> mapReturn = new HashMap<String, String>();
			String countryCode = countryService.getCountryCodeByGlnId(gtin);
			if (!Strings.isNullOrEmpty(countryCode) && countryCode.equalsIgnoreCase("ES")) {
				// Aecoc client
				try {
					mapReturn = NewAecocClient.getAecocInfo(gtin);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String caCode = postalCodeService.getCaCode(mapReturn.get("postalCode"));
				String caName = postalCodeService.getCaName(caCode);
				mapReturn.put("caCode", caCode);
				mapReturn.put("caName", caName);
				String countryName = countryService.getCountryNameByCode(countryCode);
				mapReturn.put("countryName", countryName);
			} else {
				// Gepir limited client
				String ret = "";
				try {
					ret = GepirClient.prepareConnection().data("keyValue", gtin).execute().body();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(ret);

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
			return mapReturn;
		}
	}
}
