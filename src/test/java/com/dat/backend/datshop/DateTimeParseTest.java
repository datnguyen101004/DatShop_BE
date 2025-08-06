package com.dat.backend.datshop;

import com.dat.backend.datshop.util.ConvertStringToLocalDateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DateTimeParseTest {

    @Test
    void testConvertStringToLocalDateTime() {
        // Test case for converting a valid date-time string
        String dateTimeString = "2023-10-01T12:00:01Z";
        LocalDateTime localDateTime = ConvertStringToLocalDateTime.convert(dateTimeString);
        System.out.println("Converted LocalDateTime: " + localDateTime);

        // Test case for an empty string
        String emptyString = "";
        LocalDateTime emptyResult = ConvertStringToLocalDateTime.convert(emptyString);
        System.out.println("Converted LocalDateTime from empty string: " + emptyResult);
    }
}
