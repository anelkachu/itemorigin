package itemorigin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostalResolver {

	private final static Logger LOG = LoggerFactory.getLogger(PostalResolver.class);

	static Map<String, String> mapProvCA;
	static Map<String, String> mapProv;
	static Map<String, String> mapCA;

	public static String readFile(InputStream is) throws IOException {
		String everything = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
		}
		return everything;
	}

	@PostConstruct
	public void init() {
		mapProvCA = new HashMap<String, String>();
		mapProv = new HashMap<String, String>();
		mapCA = new HashMap<String, String>();

		InputStream is = PostalResolver.class.getClassLoader().getResourceAsStream("provincias.txt");
		String contentProvicias = null;
		try {
			contentProvicias = readFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] lines = contentProvicias.split(System.getProperty("line.separator"));
		for (String line : lines) {
			String[] fields = line.split(";");
			mapProvCA.put(fields[0], fields[2]);
			mapProv.put(fields[0], fields[1]);
		}

		String contentAutonomas = null;
		is = PostalResolver.class.getClassLoader().getResourceAsStream("comAutonomas.txt");
		try {
			contentAutonomas = readFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lines = contentAutonomas.split(System.getProperty("line.separator"));
		for (String line : lines) {
			String[] fields = line.split(";");
			mapCA.put(fields[0], fields[1]);
		}
		LOG.info("PostalResolver initialized");
	}

	public static void main(String[] args) throws IOException {

		PostalResolver resolver = new PostalResolver();
		resolver.init();

		System.out.println(mapProv);
		System.out.println(mapProvCA);
		System.out.println(mapCA);

		String codigoPostal = "09400";
		String provCode = codigoPostal.substring(0, 2);
		System.out.println("Provincia: " + resolver.getProvincia(provCode));
		String caCode = resolver.getCAFromProvincia(provCode);
		System.out.println("CA: " + resolver.getCA(caCode));
	}

	public String getProvincia(String provCode) {
		return mapProv.get(provCode);
	}

	public String getCAFromProvincia(String provCode) {
		return mapProvCA.get(provCode);
	}

	public String getCA(String caCode) {
		return mapCA.get(caCode);
	}

}
