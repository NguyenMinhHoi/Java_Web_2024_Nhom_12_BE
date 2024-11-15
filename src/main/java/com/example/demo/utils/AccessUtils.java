package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class AccessUtils {
    public static boolean setAccessMerchant(NamedParameterJdbcTemplate jdbcTemplate, Long id){
        String sql = "UPDATE merchant SET last_access = CURRENT_TIMESTAMP WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    };
}
