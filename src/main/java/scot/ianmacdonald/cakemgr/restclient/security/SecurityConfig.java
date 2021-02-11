package scot.ianmacdonald.cakemgr.restclient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated().and().oauth2Login().and().logout()
		.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll();
	}

}