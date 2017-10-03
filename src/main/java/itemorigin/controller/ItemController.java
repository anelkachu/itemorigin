package itemorigin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.wnameless.json.flattener.JsonFlattener;

import itemorigin.beans.IResolver;

@RestController
public class ItemController {

	@Autowired
	IResolver solver;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping(value = "/gln/{id}", produces = "application/json")
	public String getInfoByGlnId(@PathVariable String id) throws IOException {
		return solver.getGlnInfo(id);
	}

	@RequestMapping(value = "/item/{id}", produces = "application/json")
	public Map<String, String> test(@PathVariable String id) throws IOException {
		String ret = solver.getGlnInfo(id);
		Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(ret);

		String partyName = (String) flattenedJsonMap.get("gepirParty.partyDataLine.gS1KeyLicensee.partyName");
		String street = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.streetAddressOne");
		String lastChange = (String) flattenedJsonMap.get("gepirParty.partyDataLine.lastChangeDate");
		String city = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.city");
		String countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.countryCode._");
		String countryAdministered = (String) flattenedJsonMap.get("gepirParty.partyDataLine.countryAdministered");
		String postalCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.postalCode");

		Map<String, String> mapReturn = new HashMap<String, String>(7);
		mapReturn.put("partyName", partyName);
		mapReturn.put("street", street);
		mapReturn.put("lastChange", lastChange);
		mapReturn.put("countryCode", countryCode);
		mapReturn.put("countryAdministered", countryAdministered);
		mapReturn.put("postalCode", postalCode);
		mapReturn.put("city", city);

		return mapReturn;
	}
}
