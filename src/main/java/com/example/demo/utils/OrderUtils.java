package com.example.demo.utils;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

public class OrderUtils {
    public static void updateOrderStatus(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String order_id, String newStatus) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("order_id", order_id);
        params.put("new_status", newStatus);
        namedParameterJdbcTemplate.update(Const.UPDATE_ORDER_STATUS_LOG,params);
    };
}
