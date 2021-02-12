package scot.ianmacdonald.cakemgr.restclient.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated().and().oauth2Login().and().logout()
		.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll();
	}

}