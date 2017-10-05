package itemorigin.service;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.springframework.stereotype.Component;

import itemorigin.client.StandaloneClient;

@Component
public class OriginService implements IOriginService {

	@Override
	public String getGlnInfo(String id) {
		String result = null;
		Response response;
		try {
			response = StandaloneClient.prepareConnection().data("keyValue", id).execute();
			result = response.body();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String setGlnInfo(String glnId) {
		return glnId;
	}

}
