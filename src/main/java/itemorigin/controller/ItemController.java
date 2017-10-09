package itemorigin.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import itemorigin.service.CacheService;

@RestController
public class ItemController {

	@Autowired
	CacheService cacheService;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping(value = "/item/{gtin}", produces = "application/json")
	public Map<String, String> item(@PathVariable String gtin) throws IOException {
		Stopwatch watch = Stopwatch.createStarted();
		Map<String, String> mapReturn = cacheService.getItemInfo(gtin);
		// Time response
		mapReturn.put("responseTime", String.valueOf(watch.stop().elapsed(TimeUnit.MILLISECONDS)));
		mapReturn.entrySet().removeIf(e -> Strings.isNullOrEmpty(e.getValue()));
		return mapReturn;
	}

}
