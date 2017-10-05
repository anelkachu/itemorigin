package itemorigin.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface IOriginService {

	@Cacheable("glnId")
	String getGlnInfo(String id);

	@CachePut(value = "glnId", key = "#glnId + 1")
	String setGlnInfo(String glnId);
}
