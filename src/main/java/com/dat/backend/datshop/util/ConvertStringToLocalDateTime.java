package com.dat.backend.datshop.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class ConvertStringToLocalDateTime {
    public static LocalDateTime convert(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        // Parse chuỗi thành instant
        Instant instant = Instant.parse(dateTimeString);

        // Tạo zoneId là Asia/Ho_Chi_Minh
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        // Chuyển đổi instant sang LocalDateTime với zoneId
        return instant.atZone(zoneId).toLocalDateTime();
    }
}
