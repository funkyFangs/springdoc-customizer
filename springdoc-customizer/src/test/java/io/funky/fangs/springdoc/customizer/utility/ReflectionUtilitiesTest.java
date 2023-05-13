package io.funky.fangs.springdoc.customizer.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilitiesTest {
    public final static String staticString = "static";
    public final String instanceString = "instance";

    @Test
    void getFieldValueTest() throws NoSuchFieldException {
        var field = getClass().getField("instanceString");
        String fieldValue = ReflectionUtilities.getFieldValue(field, this);

        assertThat(fieldValue).isSameAs(instanceString);
    }

    @Test
    void getFieldValueStaticTest() throws NoSuchFieldException {
        var field = getClass().getField("staticString");
        String fieldValue = ReflectionUtilities.getFieldValue(field);

        assertThat(fieldValue).isSameAs(staticString);
    }
}