package itemorigin.util;

import java.io.IOException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.net.InetAddresses;

public class JSoupUtils {

	public static final String AECOC_ENDPOINT = "http://sede.aecoc.es:8000/GEPIR/consultas/SearchByGTIN.aspx";

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	public static final int TIMEOUT = 60 * 1000;
	public static final int MAX_RETRY = 3;

	public static Document getUrlDocument(final String url) throws Exception {
		int count = 0;
		String randomIP = getRandomIp();

		boolean success = false;

		Document doc = null;

		while (!success && count < MAX_RETRY) {
			try {
				doc = Jsoup.connect(url).userAgent(USER_AGENT).header("REMOTE_ADDR", randomIP)
						.header("HTTP_X_FORWARDED", randomIP).header("HTTP_X_CLUSTER_CLIENT_IP", randomIP)
						.header("HTTP_FORWARDED_FOR", randomIP).header("HTTP_FORWARDED", randomIP).timeout(TIMEOUT)
						.get();
				success = true;
			} catch (IOException e) {
				System.out.println("Failed connection: " + e.getMessage());
				count++;
			} finally {
				if (!success) {
					throw new Exception("Can't connect to " + url);
				}
			}
		}
		return doc;
	}

	public static String getRandomIp() {
		String ipString = InetAddresses.fromInteger(new Random().nextInt()).getHostAddress();
		return ipString;
	}

}
