package itemorigin.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Strings;

import itemorigin.util.JSoupUtils;

public class AustriaClient {

	// public static void main(String[] args) throws IOException, ParseException {
	// String url =
	// "http://www.gepiraustria.org/en/Home/SearchGepirCH/?sort=&page=1&pageSize=10&group=&filter=&term=08430094304074&countryChoice=AT&identKeyChoice=GTIN&gepirVersion=&street=&postalCode=&city=&advanced=false";
	//
	// String randomIP = JSoupUtils.getRandomIp();
	// for (int i = 0; i < 1; i++) {
	// Connection.Response response =
	// Jsoup.connect(url).userAgent(JSoupUtils.USER_AGENT).ignoreContentType(true)
	// .header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP)
	// .header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP",
	// randomIP)
	// .header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
	// .method(Connection.Method.GET).execute();
	//
	// JSONParser parser = new JSONParser();
	// String ret = response.body();
	// Object obj = parser.parse(ret);
	// JSONObject jsonObject = (JSONObject) obj;
	// Map<String, Object> flattenedJsonMap =
	// JsonFlattener.flattenAsMap(jsonObject.toString());
	//
	// // flattenedJsonMap.forEach((k, v) -> System.out.println(k + " : " +
	// // v));
	// System.out.println(response.body());
	// System.out.println("Request #" + i + " : " +
	// flattenedJsonMap.get("Data[0].companyInfo.partyName"));
	// }
	// }

	public static void main(String[] args) throws IOException {
		System.out.println(getGepirAustriaInfo("8410728180120"));
	}

	public static Map<String, String> getGepirAustriaInfo(String gtin) throws IOException {
		Map<String, String> mapReturn = new HashMap<String, String>();
		String url = "http://www.gepiraustria.org/en/Home/SearchGepirCH/?sort=&page=1&pageSize=10&group=&filter=&term="
				+ gtin + "&countryChoice=AT&identKeyChoice=GTIN&gepirVersion=&street=&postalCode=&city=&advanced=false";

		String randomIP = JSoupUtils.getRandomIp();
		Connection.Response response = Jsoup.connect(url).userAgent(JSoupUtils.USER_AGENT).ignoreContentType(true)
				.header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP).header("HTTP_X_FORWARDED", randomIP)
				.header("HTTP_X_CLUSTER_CLIENT_IP", randomIP).header("HTTP_FORWARDED_FOR", randomIP)
				.header("HTTP_FORWARDED", randomIP).method(Connection.Method.GET).execute();

		String jsonContent = response.body();

		Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonContent);

		String address = (String) flattenedJsonMap.get("Data[0].companyInfo.address.streetAddressOne");
		String postalCode = (String) flattenedJsonMap.get("Data[0].companyInfo.address.postalCode");
		String city = (String) flattenedJsonMap.get("Data[0].companyInfo.address.city");
		String partyName = (String) flattenedJsonMap.get("Data[0].companyInfo.partyName");

		mapReturn.put("address", address);
		mapReturn.put("postalCode", postalCode);
		mapReturn.put("city", city);
		mapReturn.put("partyName", partyName);

		return mapReturn;

	}

	public static Map<String, String> getGepirAustriaInfo() {
		Map<String, String> mapReturn = new HashMap<String, String>();
		String jsonContent = "";
		try {
			jsonContent = new String(Files
					.readAllBytes(Paths.get("C:\\Users\\inre\\git\\itemorigin\\src\\main\\resources\\austria.json")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonContent);

		// System.out.println(flattenedJsonMap);
		// System.out.println(flattenedJsonMap.get("Data[0].companyInfo.partyName"));

		String address = (String) flattenedJsonMap.get("Data[0].companyInfo.address.streetAddressOne");
		String postalCode = (String) flattenedJsonMap.get("Data[0].companyInfo.address.postalCode");
		String city = (String) flattenedJsonMap.get("Data[0].companyInfo.address.city");
		String partyName = (String) flattenedJsonMap.get("Data[0].companyInfo.partyName");

		mapReturn.put("address", address);
		mapReturn.put("postalCode", postalCode);
		mapReturn.put("city", city);
		mapReturn.put("partyName", partyName);

		return mapReturn;
	}

}
