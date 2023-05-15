package com.imss.sivimss.notasremision.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@TestConfiguration
//@Order(1)
public class TestSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {
    //@Override
    protected  void configure(HttpSecurity httpSecurity) throws Exception{
        //httpSecurity.authorizeRequests().anyRequest().permitAll();
        /*httpSecurity.authorizeRequests()
                .mvcMatchers(JsonAuthenticationFilter.AUTH_PASS).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();*/
        httpSecurity.csrf().disable()
                .authorizeRequests().anyRequest().permitAll();
    }
}
