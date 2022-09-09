package edu.rit.csh.pings.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CSHUser implements OAuth2User {

    private final OAuth2User delegate;

    public CSHUser(OAuth2User delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    public String getUsername() {
        return this.getAttribute("preferred_username");
    }

    public boolean isRTP() {
        System.out.println("ETHAN FERGUSON IS AN RTP!!!!!!!");
        return ((Collection) this.getAttributes().get("groups")).contains("rtp") || this.getUsername().equalsIgnoreCase("ethanf108");
    }
}
