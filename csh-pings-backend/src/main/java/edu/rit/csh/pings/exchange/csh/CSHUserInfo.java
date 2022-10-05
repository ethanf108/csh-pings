package edu.rit.csh.pings.exchange.csh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CSHUserInfo {

    boolean rtp;
    private String username;
    private String fullName;
}
