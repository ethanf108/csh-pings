package edu.rit.csh.pings.servicereflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metadata for both the ServiceConfiguration classes and properties in them.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurableProperty {

    /**
     * Name of the Service, or name of the Property. Required.
     *
     * @return identifiable name, /[a-zA-Z0-9_-]/
     */
    String name();

    /**
     * Long description of the Service, or the Property.
     *
     * @return description text
     */
    String description() default "";

    /**
     * Used on Configurations.
     *
     * @return true if multiple configurations per user are allowed
     */
    boolean allowMultiple() default true;
}
