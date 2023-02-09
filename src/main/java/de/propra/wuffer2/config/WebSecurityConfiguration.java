package de.propra.wuffer2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

  @Bean
  public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
    chainBuilder.authorizeHttpRequests(
            configurer -> configurer
                .antMatchers("/css/*").permitAll()
                .anyRequest().authenticated()
        )
        .oauth2Login()
        .and()
        .csrf().disable()
        .headers().frameOptions().disable();

    return chainBuilder.build();
  }
}