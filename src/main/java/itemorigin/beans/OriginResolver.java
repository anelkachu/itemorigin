package itemorigin.beans;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection.Response;
import org.springframework.stereotype.Component;

import itemorigin.client.StandaloneClient;

@Component
public class OriginResolver implements IResolver {

	@Override
	public String getGlnInfo(String id) {
		String result = null;
		try {
			TimeUnit.SECONDS.sleep(5); // (1)
			Response response = StandaloneClient.prepareConnection().data("keyValue", id).execute();
			result = response.body();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
