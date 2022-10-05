package edu.rit.csh.pings.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {

    private final DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();

    @Value("${csh.pings.admins}")
    private String admins;

    private CSHUser getUser(OAuth2UserRequest request) {
        return new CSHUser(this.defaultService.loadUser(request));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CSHUser.pingsAdmins = new HashSet<>(Arrays
                .stream(this.admins.split(","))
                .map(String::toLowerCase)
                .map(String::strip)
                .toList());

        http
                .csrf().disable()
                .authorizeRequests().antMatchers("/service/**").permitAll().and()
                .authorizeRequests().anyRequest().authenticated().and()
                .oauth2Login(oauthLogin -> oauthLogin
                        .userInfoEndpoint()
                        .userService(this::getUser));
    }
}
