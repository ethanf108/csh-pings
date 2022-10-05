package edu.rit.csh.pings.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CSHUser implements OAuth2User {

    /**
     * the {@code @Value} does nothing, just here for reference
     * A list of all Pings Admins, or, those who have the power
     * of RTPs in the Pings project
     * <p>
     * TODO: Make it so that this doesn't have to statically set because this is cringe.
     */
    @Value("${csh.pings.admins}")
    static Set<String> pingsAdmins;

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

    public String getFullName() {
        return this.getAttribute("name");
    }

    public String getUsername() {
        return this.getAttribute("preferred_username");
    }

    public boolean isRTP() {
        return ((Collection<?>) this.getAttributes().get("groups")).contains("rtp") || pingsAdmins.contains(this.getUsername().toLowerCase());
    }
}
