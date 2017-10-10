package itemorigin.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Splitter;

import itemorigin.util.JSoupUtils;

public class NewAecocClient {

	public static final String URL = "http://sede.aecoc.es:8000/GEPIR/consultas/SearchByGTIN.aspx";

	public static void main(String[] args) throws IOException {
		// String gtin = "8414807525604";
		String gtin = "8430094304074";

		for (int j = 0; j < 40; j++) {
			String content = getHTML(gtin);
			org.jsoup.nodes.Document doc = Jsoup.parse(content, "UTF-8");

			Elements elements = doc.select("#divResul span");

			Map<String, String> mapReturn = new HashMap<String, String>();

			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblDireccion")) {
					mapReturn.put("address", e.text());
				}
				if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblCiudad")) {
					String codeAndCity = e.text();
					Pair<String, String> data = extractCityData(codeAndCity);
					mapReturn.put("postalCode", data.getLeft());
					mapReturn.put("city", data.getRight());
				}
				if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblPartyName")) {
					mapReturn.put("partyName", e.text());
				}
			}
			System.out.println("REQUEST #" + j + " : " + mapReturn);
		}

	}

	public static Map<String, String> getAecocInfo(String gtin) throws IOException {
		Map<String, String> mapReturn = new HashMap<String, String>();
		String stringToSplit = "_ga=GA1.2.78114108.1507537150; _gid=GA1.2.774249171.1507537150";

		final Map<String, String> splitKeyValues = Splitter.on(";").omitEmptyStrings().trimResults()
				.withKeyValueSeparator("=").split(stringToSplit);

		String randomIP = JSoupUtils.getRandomIp();
		Elements elements = Jsoup.connect(URL).userAgent(JSoupUtils.USER_AGENT).header("Connection", "keep-alive")
				.header("REMOTE_ADDR", randomIP).header("HTTP_X_FORWARDED", randomIP)
				.header("HTTP_X_CLUSTER_CLIENT_IP", randomIP).header("HTTP_FORWARDED_FOR", randomIP)
				.header("HTTP_FORWARDED", randomIP).method(Connection.Method.POST).cookies(splitKeyValues)
				.data("__VIEWSTATE",
						"/wEPDwUKLTUxMzc5NTk2Mg9kFgICAw9kFgICCQ8PFgIeBFRleHRlZGQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgQFDXJkUGFydHlCeUdUSU4FCnJkSXRlbUdUSU4FCnJkSXRlbUdUSU4FCGJ0QnVzY2Fy2xF/XkE/1XkbtWDtD9/1m64zoUE=")
				.data("__VIEWSTATEGENERATOR", "4E866143")
				.data("__EVENTVALIDATION", "/wEWBQKk+8XBBAKg7LqFDQLCzuKzDQLS29mZDwLqoIaxAaTPvEwK8IuoEKohwjYsPn/+syQc")
				.data("txtGTIN", gtin).data("radios", "rdPartyByGTIN").data("btBuscar.x", "33").data("btBuscar.y", "16")
				.execute().parse().select("#divResul span");

		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblDireccion")) {
				mapReturn.put("address", e.text());
			}
			if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblCiudad")) {
				String codeAndCity = e.text();
				Pair<String, String> data = extractCityData(codeAndCity);
				mapReturn.put("postalCode", data.getLeft());
				mapReturn.put("city", data.getRight());
			}
			if (e.attr("id").equalsIgnoreCase("rptListadoPartyByGTIN_ctl01_lblPartyName")) {
				mapReturn.put("partyName", e.text());
			}
		}
		return mapReturn;
	}

	private static Pair<String, String> extractCityData(String codeAndCity) {
		String code = codeAndCity.substring(0, 5).trim();
		String city = codeAndCity.substring(6).trim();
		Pair<String, String> ret = Pair.of(code, city);
		return ret;
	}

	private static String getHTML(String gtin) {
		String ret = "";
		String stringToSplit = "_ga=GA1.2.78114108.1507537150; _gid=GA1.2.774249171.1507537150";

		final Map<String, String> splitKeyValues = Splitter.on(";").omitEmptyStrings().trimResults()
				.withKeyValueSeparator("=").split(stringToSplit);

		String randomIP = JSoupUtils.getRandomIp();
		try {
			Connection.Response response = Jsoup.connect(URL).userAgent(JSoupUtils.USER_AGENT)
					.header("Connection", "keep-alive").header("REMOTE_ADDR", randomIP)
					.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
					.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP)
					.method(Connection.Method.POST).cookies(splitKeyValues)
					.data("__VIEWSTATE",
							"/wEPDwUKLTUxMzc5NTk2Mg9kFgICAw9kFgICCQ8PFgIeBFRleHRlZGQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgQFDXJkUGFydHlCeUdUSU4FCnJkSXRlbUdUSU4FCnJkSXRlbUdUSU4FCGJ0QnVzY2Fy2xF/XkE/1XkbtWDtD9/1m64zoUE=")
					.data("__VIEWSTATEGENERATOR", "4E866143")
					.data("__EVENTVALIDATION",
							"/wEWBQKk+8XBBAKg7LqFDQLCzuKzDQLS29mZDwLqoIaxAaTPvEwK8IuoEKohwjYsPn/+syQc")
					.data("txtGTIN", gtin).data("radios", "rdPartyByGTIN").data("btBuscar.x", "33")
					.data("btBuscar.y", "16").execute();
			ret = response.body();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
