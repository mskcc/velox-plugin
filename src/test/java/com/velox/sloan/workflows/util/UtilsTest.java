package com.velox.sloan.workflows.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UtilsTest {
    @Test
    public void whenValueIfNull_shouldRetunEmptyString() throws Exception {
        String formattedValue = Utils.getFormattedValue(null);

        assertThat(formattedValue, is("\"\""));
    }

    @Test
    public void whenValueIfEmpty_shouldRetunEmptyString() throws Exception {
        String formattedValue = Utils.getFormattedValue("");

        assertThat(formattedValue, is("\"\""));
    }

    @Test
    public void whenValueIsNotEmpty_shouldRetunQuotedString() throws Exception {
        String value = "sialalala";
        String formattedValue = Utils.getFormattedValue(value);

        assertThat(formattedValue, is("\"" + value + "\""));
    }

}