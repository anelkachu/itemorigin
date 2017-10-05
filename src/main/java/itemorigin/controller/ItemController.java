package itemorigin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import itemorigin.service.CountryService;
import itemorigin.service.IOriginService;
import itemorigin.service.PostalCodeService;

@RestController
public class ItemController {

	@Autowired
	IOriginService solver;

	@Autowired
	CountryService countryService;

	@Autowired
	PostalCodeService postalCodeService;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping(value = "/gln/{id}", produces = "application/json")
	public String getInfoByGlnId(@PathVariable String id) throws IOException {
		return solver.getGlnInfo(id);
	}

	@RequestMapping(value = "/item/{id}", produces = "application/json")
	public Map<String, String> test(@PathVariable String id, HttpServletResponse response) throws IOException {
		Stopwatch watch = Stopwatch.createStarted();
		Map<String, String> mapReturn = new HashMap<String, String>();

		String ret = solver.getGlnInfo(id);
		Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(ret);

		String partyName = (String) flattenedJsonMap.get("gepirParty.partyDataLine.gS1KeyLicensee.partyName");
		String street = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.streetAddressOne");
		String lastChange = (String) flattenedJsonMap.get("gepirParty.partyDataLine.lastChangeDate");
		String city = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.city");
		String postalCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.postalCode");
		// Country
		String countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.countryCode._");
		if (Strings.isNullOrEmpty(countryCode)) {
			countryCode = countryService.getCountryCodeByGlnId(id);
		}
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

		// Time response
		mapReturn.put("responseTime", String.valueOf(watch.stop().elapsed(TimeUnit.MILLISECONDS)));

		mapReturn.entrySet().removeIf(e -> Strings.isNullOrEmpty(e.getValue()));
		return mapReturn;
	}
}
