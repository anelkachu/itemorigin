package itemorigin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	@RequestMapping(value = "/testCompleto", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> test() throws IOException {
		Map<String, String> mapReturn = new HashMap<String, String>();
		mapReturn.put("partyName", "ALTER FARMACIA, S.A.");
		mapReturn.put("caName", "Comunidad de Madrid");
		mapReturn.put("city", "Madrid");
		mapReturn.put("address", "CL Mateo Inurria, 30");
		mapReturn.put("countryCode", "ES");
		mapReturn.put("responseTime", "123");
		mapReturn.put("postalCode", "28050");
		mapReturn.put("lastChange", "2017-10-06T01:53:05.422");
		mapReturn.put("caCode", "13");
		mapReturn.put("countryName", "España");
		return mapReturn;
	}
}
