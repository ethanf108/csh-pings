package edu.rit.csh.pings.servicereflect;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metadata for both the ServiceConfiguration classes and properties in them.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurableProperty {

    /**
     * @return id of the property
     */
    String id();

    /**
     * Name of the Property. Required.
     *
     * @return identifiable name, /[a-zA-Z0-9_-]/
     */
    String name();

    /**
     * Long description of the Property.
     *
     * @return description text
     */
    String description() default "";

    /**
     * Type of data to represent
     *
     * @return {@code Type}
     */
    Type type();

    /**
     * @return Regex to be used to validate data
     */
    String validationRegex() default ".*";

    /**
     * Only used if {@code type()} is {@code ENUM}
     *
     * @return possible values
     */
    String[] enumValues() default {};

    @Getter
    @AllArgsConstructor
    enum Type {
        TEXT("text"),
        MULTILINE_TEXT("textarea"),
        EMAIL("email"),
        URL("url"),
        TEL("tel"),
        ENUM("select"),
        NUMBER("number"),
        BOOL("checkbox");

        private final String htmlInputType;
    }
}
