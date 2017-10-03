package itemorigin;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FilterRegistrationBean responseFilter() {
		FilterRegistrationBean hostRegistration = new FilterRegistrationBean();
		Filter filter = new Filter() {

			@Override
			public void destroy() {
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				HttpServletResponse resp = (HttpServletResponse) response;
				resp.setHeader("Access-Control-Allow-Origin", "*");
				resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
				resp.setHeader("Access-Control-Max-Age", "3600");
				resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json; charset=UTF-8");
				resp.setHeader("Content-Encoding", "application/json; charset=UTF-8");
				chain.doFilter(request, response);
			}

			@Override
			public void init(FilterConfig arg0) throws ServletException {
			}

		};
		hostRegistration.setFilter(filter);
		hostRegistration.setEnabled(true);
		return hostRegistration;
	}

}
