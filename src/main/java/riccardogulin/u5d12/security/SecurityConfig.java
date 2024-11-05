package riccardogulin.u5d12.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// Annotazione per stabilire che questa non sarà una classe di configurazione qualsiasi, ma sarà dedicata a configurare Spring Security
@EnableMethodSecurity // Se voglio poter utilizzare le regole di AUTORIZZAZIONE con @PreAuthorize è OBBLIGATORIA questa annotazione
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// Per poter configurare tutto ciò che è relativo alla sicurezza devo configurare Spring Security tramite questo apposito bean, il quale
		// mi consentirà di:
		// - disabilitare comportamenti di default che non ci interessano
		httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable()); // Non voglio il form di login (avremo React per quello)
		httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()); // Non voglio la protezione da CSRF (perché non ci serve
		// ed inoltre mi complicherebbe anche il lato FE)
		httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer ->
				httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		// Non vogliamo utilizzare le Sessioni (perché JWT NON utilizza le sessioni)
		httpSecurity.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry.requestMatchers("/**").permitAll()); // Disabilitiamo il 401 che riceviamo di default
		// per OGNI richiesta che facciamo su OGNI endpoint
		// - personalizzare il comportamento di alcune funzionalità preesistenti
		// - aggiungere filtri personalizzati alla Filter Chain
		return httpSecurity.build();
	}

	@Bean
	PasswordEncoder getBCrypt() {
		return new BCryptPasswordEncoder(12);
	}
}
