package itemorigin.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

	@Bean
	public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
		return new BeanFactoryPostProcessor() {
			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				BeanDefinition bean = beanFactory.getBeanDefinition(
						DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
				bean.getPropertyValues().add("loadOnStartup", 1);
			}
		};
	}

	// CORS filter
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
