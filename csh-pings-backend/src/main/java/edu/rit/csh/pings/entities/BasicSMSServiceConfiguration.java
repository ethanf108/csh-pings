package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ServiceDescription(name = "sms", description = "SMS / MMS")
public final class BasicSMSServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(name = "phone-num", description = "Phone Number", type = ConfigurableProperty.Type.TEL)
    private String phoneNum;

    @Column
    @ConfigurableProperty(
            name = "carrier",
            description = "Carrier",
            type = ConfigurableProperty.Type.ENUM,
            enumValues = {
                    "AT&T",
                    "Boost Mobile",
                    "Sprint",
                    "T-Mobile",
                    "Verizon"
            })
    private String carrier;

    @Column
    @ConfigurableProperty(
            name = "method",
            description = "SMS or MMS",
            type = ConfigurableProperty.Type.ENUM,
            enumValues = {"sms", "mms"})
    private String method;

    @Override
    public void create(Map<String, String> properties) {
        final String phoneNum = properties.get("phone-num");
        if (!phoneNum.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Invalid PhoneNumber");
        }
        this.phoneNum = phoneNum;
        final String carrier = properties.get("carrier");
        try {
            if (!Arrays.asList(this.getClass().getDeclaredField("carrier").getAnnotation(ConfigurableProperty.class).enumValues()).contains(carrier)) {
                throw new IllegalArgumentException("Invalid Carrier Value");
            }
        } catch (NoSuchFieldException e) {
            throw new Error("How", e);
        }
        this.carrier = carrier;
        final String method = properties.get("method");
        if (!method.equalsIgnoreCase("sms") && !method.equalsIgnoreCase("mms")) {
            throw new IllegalArgumentException("Invalid Method");
        }
        this.method = method;
    }
}
