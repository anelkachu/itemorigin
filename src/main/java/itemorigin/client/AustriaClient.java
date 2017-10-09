package itemorigin.client;

import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.wnameless.json.flattener.JsonFlattener;

import itemorigin.util.JSoupUtils;

public class AustriaClient {

	public static void main(String[] args) throws IOException, ParseException {
		String url = "http://www.gepiraustria.org/en/Home/SearchGepirCH/?sort=&page=1&pageSize=10&group=&filter=&term=08430094304074&countryChoice=AT&identKeyChoice=GTIN&gepirVersion=&street=&postalCode=&city=&advanced=false";

		String randomIP = JSoupUtils.getRandomIp();
		for (int i = 0; i < 50; i++) {
			Connection.Response response = Jsoup.connect(url).userAgent(JSoupUtils.USER_AGENT).ignoreContentType(true)
					.header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP)
					.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
					.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
					.method(Connection.Method.GET).execute();

			JSONParser parser = new JSONParser();
			String ret = response.body();
			Object obj = parser.parse(ret);
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonObject.toString());

			// flattenedJsonMap.forEach((k, v) -> System.out.println(k + " : " +
			// v));
			// System.out.println(response.body());
			System.out.println("Request #" + i + " : " + flattenedJsonMap.get("Data[0].companyInfo.partyName"));
		}
	}

}
