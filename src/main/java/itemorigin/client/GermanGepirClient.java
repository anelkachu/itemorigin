package itemorigin.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import itemorigin.util.JSoupUtils;

public class GermanGepirClient {

	private static final Map<String, String> MAP_PARAMS = Splitter.on(";").omitEmptyStrings().trimResults()
			.withKeyValueSeparator("=")
			.split("_jsuid=404486580; 41e4c95d7c759d614046ee36c27d0981=d142fe8d88399cf54b40c0c23f9676e6; cb-enabled=accepted; _first_pageview=1; _eventqueue=%7B%22heatmap%22%3A%5B%5D%2C%22events%22%3A%5B%5D%7D; _gat_UA-889776-1=1; _ga=GA1.3.1776705559.1506691823; _gid=GA1.3.1738639756.1506691823");

	private static final String URL = "http://gepir.gs1.org/index.php?option=com_gepir4ui&view=getkeylicensee&format=raw";

	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		// String gln = "8423415501009";
		// String gln = "8410728180120";

		String[] items = new String[] { "640522710850", "6970273110000", "8410055150018", "8430094304074",
				"8423415501009" };

		for (int i = 0; i < 50; i++) {
			String gtin = items[i % items.length];
			String randomIP = JSoupUtils.getRandomIp();
			String url = "https://www.gepir.de/rest/search?q=" + gtin;
			Connection.Response response = Jsoup.connect(url).userAgent(JSoupUtils.USER_AGENT)
					.header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP)
					.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
					.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
					.method(Connection.Method.GET).ignoreContentType(true).execute();

			JSONParser parser = new JSONParser();
			String ret = response.body();
			Object obj = parser.parse(ret);
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonObject.toString());
			flattenedJsonMap.forEach((k, v) -> System.out.println(k + " : " + v));
			System.out.println("End of request " + i + " **************************");
			Thread.sleep(1500);
		}

	}

	public static Connection prepareConnection() {
		String randomIP = JSoupUtils.getRandomIp();
		Connection conn = Jsoup.connect(URL).userAgent(JSoupUtils.USER_AGENT).header("REMOTE_ADDR", randomIP)
				.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
				.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
				.method(Connection.Method.POST).cookies(MAP_PARAMS)/* .data("keyValue", gln) */
				.data("requestTradeItemType", "ownership").data("dccac7287b2ac6b801dafa79048758ef", "1")
				.data("keyCode", "gtin");
		return conn;
	}

	public static Map<String, String> getGepirInfo(String gtin) throws IOException {
		String ret = "";
		Map<String, String> mapReturn = new HashMap<String, String>();
		try {
			ret = GermanGepirClient.prepareConnection().data("keyValue", gtin).execute().body();
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
		String countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.address.countryCode._");
		if (Strings.isNullOrEmpty(countryCode)) {
			countryCode = (String) flattenedJsonMap.get("gepirParty.partyDataLine.countryAdministered");
		}
		mapReturn.put("partyName", partyName);
		mapReturn.put("address", street);
		mapReturn.put("lastChange", lastChange);
		mapReturn.put("countryCode", countryCode);
		mapReturn.put("postalCode", postalCode);
		mapReturn.put("city", city);
		return mapReturn;
	}

}
