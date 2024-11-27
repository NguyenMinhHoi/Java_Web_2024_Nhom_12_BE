package com.example.demo.utils;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

public class CodeUtils {
    public static String generateVariantCode(String prefix) {
        int randomNumber = (int) (Math.random() * 900000) + 100000;
        return prefix + randomNumber;
    }

    public static String generateVoucherCode(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        String prefix = "VC";
        boolean exists = true;
        String randomPart = new String();
        while (exists) {
            randomPart = String.format("%06d", (int) (Math.random() * 1000000));
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", prefix + randomPart);
            exists = Boolean.TRUE.equals(namedParameterJdbcTemplate.query(
                    "SELECT COUNT(*) > 0 FROM voucher WHERE code = :id",
                    params,
                    rs -> {
                        if (rs.next()) {
                            return rs.getBoolean(1);
                        }
                        return false;
                    }
            ));
        }
        return prefix + randomPart;
    }
}
