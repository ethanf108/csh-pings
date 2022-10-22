package edu.rit.csh.pings.servicereflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceDescription {

    /**
     * @return Identifier of the Service
     */
    String id();

    /**
     * @return Name of the Service
     */
    String name();

    /**
     * @return Description of the Service (help text kinda)
     */
    String description() default "";

    /**
     * @return true if multiple configurations per user are allowed
     */
    boolean allowMultiple() default true;
}
