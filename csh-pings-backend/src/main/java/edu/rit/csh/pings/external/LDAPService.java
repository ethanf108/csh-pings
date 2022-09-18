package edu.rit.csh.pings.external;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LDAPService {

    private final LdapTemplate ldap;

    public boolean isValidUsername(String username) {
        return !this.ldap.search(
                "cn=users,cn=accounts,dc=csh,dc=rit,dc=edu",
                "uid=" + username,
                (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get()).isEmpty();
    }
}
