package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectUtil {

    private static Logger log = LoggerFactory.getLogger(ObjectUtil.class);
    private static Pattern pattern;
    private static Matcher matcher;

    @SuppressWarnings("unchecked")
    public static <T> void mergeObjects(T dest, T source) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = dest.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(dest);
            Object value2 = field.get(source);
            Object value = null;
            if (field.getDeclaredAnnotation(IgnoreUpdate.class) != null && value1 != null) {
                value = value1;
            } else {
                value = (value1 != null && value2 == null) ? value1 : value2;
            }
            field.set(dest, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cloneObject(T source) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            Class<?> fieldTypeClazz = field.getType();
            if (field.getDeclaredAnnotation(IgnoreCopy.class) == null) {
                field.setAccessible(true);
                field.set(returnValue, field.get(source));
            }
        }
        return (T) returnValue;
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

    public static boolean validateField(String input, String regexFormat) {
        if (StringUtils.isBlank(input)) {
            return true;
        }

        pattern = Pattern.compile(regexFormat);
        matcher = pattern.matcher(input);
        return !matcher.matches();
    }
}
