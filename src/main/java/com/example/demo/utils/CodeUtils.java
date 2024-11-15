package com.example.demo.utils;

public class CodeUtils {
    public static String generateVariantCode(String prefix) {
        int randomNumber = (int) (Math.random() * 900000) + 100000;
        return prefix + randomNumber;
    }
}
