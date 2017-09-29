package itemorigin.beans;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface IResolver {

	@Cacheable("glnId")
	String getGlnInfo(String id);

	@CachePut(value = "glnId", key = "#glnId + 1")
	String setGlnInfo(String glnId);
}
