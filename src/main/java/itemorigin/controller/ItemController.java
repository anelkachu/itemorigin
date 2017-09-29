package itemorigin.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import itemorigin.beans.IResolver;

@RestController
public class ItemController {

	@Autowired
	IResolver solver;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping("/gln/{id}")
	public String getInfoByGlnId(@PathVariable String id) throws IOException {
		return solver.getGlnInfo(id);
	}
}
