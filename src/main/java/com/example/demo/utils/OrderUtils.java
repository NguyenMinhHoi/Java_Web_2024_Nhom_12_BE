package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

@Slf4j
public class OrderUtils {

    public static void updateOrderStatus(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String order_id, String newStatus) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("orderId", order_id);
        params.put("status", newStatus);
        namedParameterJdbcTemplate.update(Const.UPDATE_ORDER_STATUS_LOG,params);
    };
    
    public static String generateOrderCode(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        String prefix = "ORD";
        boolean exists = true;
        String randomPart = new String();
        while (exists) {
            randomPart = String.format("%06d", (int) (Math.random() * 1000000));
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", prefix + randomPart);
            exists = Boolean.TRUE.equals(namedParameterJdbcTemplate.query(
                    "SELECT COUNT(*) > 0 FROM orders WHERE order_code = :id",
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
