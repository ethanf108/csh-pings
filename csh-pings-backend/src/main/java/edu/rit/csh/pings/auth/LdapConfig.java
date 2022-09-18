package edu.rit.csh.pings.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {


    @Value("${csh.pings.ldap.url}")
    private String LDAP_URL;
    @Value("${csh.pings.ldap.dn}")
    private String LDAP_DN;

    @Value("${csh.pings.ldap.password}")
    private String LDAP_PASSWORD;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ret = new LdapContextSource();
        ret.setUrl(this.LDAP_URL);
        ret.setUserDn(this.LDAP_DN);
        ret.setPassword(this.LDAP_PASSWORD);
        return ret;
    }

    @Bean
    public LdapTemplate template() {
        return new LdapTemplate(this.contextSource());
    }
}
