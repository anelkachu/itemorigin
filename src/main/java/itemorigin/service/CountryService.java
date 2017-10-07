package itemorigin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

@Component
public class CountryService {

	IMap<String, String> map = Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("glnCountryCode");

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		InputStream is = CountryService.class.getClassLoader().getResourceAsStream("glnCountry.csv");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				map.put(fields[0], fields[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO i18n
		Locale.setDefault(new Locale("es", "ES"));
	}

	public String getCountryNameByCode(String alpha2Code) {
		Locale obj = new Locale("", alpha2Code);
		return obj.getDisplayCountry();
	}

	public String getCountryCodeByGlnId(String glnId) {
		return map.get(glnId.substring(0, 3));
	}

}
