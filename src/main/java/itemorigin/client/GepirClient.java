package itemorigin.client;

import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Splitter;

import itemorigin.util.JSoupUtils;

public class GepirClient {

	private static final Map<String, String> MAP_PARAMS = Splitter.on(";").omitEmptyStrings().trimResults()
			.withKeyValueSeparator("=")
			.split("_jsuid=404486580; 41e4c95d7c759d614046ee36c27d0981=d142fe8d88399cf54b40c0c23f9676e6; cb-enabled=accepted; _first_pageview=1; _eventqueue=%7B%22heatmap%22%3A%5B%5D%2C%22events%22%3A%5B%5D%7D; _gat_UA-889776-1=1; _ga=GA1.3.1776705559.1506691823; _gid=GA1.3.1738639756.1506691823");

	private static final String URL = "http://gepir.gs1.org/index.php?option=com_gepir4ui&view=getkeylicensee&format=raw";

	public static void main(String[] args) throws IOException, ParseException {
		// String gln = "8423415501009";
		String gln = "8410728180120";

		String url = "http://gepir.gs1.org/index.php?option=com_gepir4ui&view=getkeylicensee&format=raw";
		String stringToSplit = "_jsuid=404486580; 41e4c95d7c759d614046ee36c27d0981=d142fe8d88399cf54b40c0c23f9676e6; cb-enabled=accepted; _first_pageview=1; _eventqueue=%7B%22heatmap%22%3A%5B%5D%2C%22events%22%3A%5B%5D%7D; _gat_UA-889776-1=1; _ga=GA1.3.1776705559.1506691823; _gid=GA1.3.1738639756.1506691823";

		final Map<String, String> splitKeyValues = Splitter.on(";").omitEmptyStrings().trimResults()
				.withKeyValueSeparator("=").split(stringToSplit);

		String randomIP = JSoupUtils.getRandomIp();
		for (int i = 0; i < 1; i++) {
			Connection.Response response = Jsoup.connect(url).userAgent(JSoupUtils.USER_AGENT)
					.header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP)
					.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
					.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
					.method(Connection.Method.POST).cookies(splitKeyValues).data("keyValue", gln)
					.data("requestTradeItemType", "ownership").data("dccac7287b2ac6b801dafa79048758ef", "1")
					.data("keyCode", "gtin").execute();

			Map<String, String> cookies = response.cookies();

			JSONParser parser = new JSONParser();
			String ret = response.body();
			Object obj = parser.parse(ret);
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(jsonObject.toString());

			// flattenedJsonMap.forEach((k, v) -> System.out.println(k + " : " +
			// v));
			// System.out.println(response.body());
			System.out.println("Request #" + i + " : " + flattenedJsonMap.get("gepirParty.partyDataLine.address.name"));
		}

	}

	public static Connection prepareConnection() {
		String randomIP = JSoupUtils.getRandomIp();
		Connection conn = Jsoup.connect(URL).userAgent(JSoupUtils.USER_AGENT).header("REMOTE_ADDR", randomIP)
				.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
				.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
				.method(Connection.Method.POST)
				.cookies(MAP_PARAMS)/* .data("keyValue", gln) */
				.data("requestTradeItemType", "ownership").data("dccac7287b2ac6b801dafa79048758ef", "1")
				.data("keyCode", "gtin");
		return conn;
	}

}
