package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class CommonUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty() || collection.stream().anyMatch(Objects::isNull);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Object object) {
        return object == null;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0 || array[0] == null;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    /*default zone id is Viet Nam*/
    public static String currentDateTimeByZoneId(String zoneId, String format, int addMinutes) {
        if (isEmpty(zoneId)) {
            zoneId = "Asia/Ho_Chi_Minh";
        }

        Instant now = Instant.now().plus(addMinutes, ChronoUnit.MINUTES);

        // Chuyển đổi Instant thành ZonedDateTime
        ZoneId zoneIdFormat = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = now.atZone(zoneIdFormat);

        // Định dạng ZonedDateTime thành chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

    public static String convertObjectToJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Chuyển đổi Object sang chuỗi JSON
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
