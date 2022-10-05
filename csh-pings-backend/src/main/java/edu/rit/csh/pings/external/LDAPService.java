package edu.rit.csh.pings.external;

import edu.rit.csh.pings.exchange.csh.CSHUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

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

    private CSHUserInfo userInfoMapper(Attributes attrs) throws NamingException {
        final CSHUserInfo ret = new CSHUserInfo();
        ret.setRtp(false);
        if (attrs.get("memberOf") != null) {
            for (int i = 0; i < attrs.get("memberOf").size(); i++) {
                if (attrs.get("memberOf").get(i).toString().startsWith("cn=rtp,")) {
                    ret.setRtp(true);
                }
            }
        }
        ret.setUsername(attrs.get("uid").get().toString());
        ret.setFullName(attrs.get("cn").get().toString());
        return ret;
    }

    public List<CSHUserInfo> searchUsers(String search) {
        if (search.isBlank()) {
            return List.of();
        }
        return this.ldap.search(
                "cn=accounts,dc=csh,dc=rit,dc=edu",
                "(&(objectClass=cshMember)(|(cn=*" + search + "*)(uid=*" + search + "*)))",
                this::userInfoMapper);
    }

    public CSHUserInfo getUser(String username) {
        List<CSHUserInfo> ret = this.ldap.search(
                "cn=accounts,dc=csh,dc=rit,dc=edu",
                "(&(objectClass=cshMember)(uid=" + username + "))",
                this::userInfoMapper);
        return ret.isEmpty() ? null : ret.get(0);
    }
}
